package com.unifin.jirareports.business.jira;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.rest.GroupResponse;
import com.unifin.jirareports.model.rest.SearchIssueResponse;
import com.unifin.jirareports.model.rest.WorklogResponse;
import com.unifin.jirareports.util.JsonUtil;
import com.unifin.jirareports.model.jira.ConsultoriaEnum;
import com.unifin.jirareports.model.jira.GroupDTO;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

/**
 * BusinessClientJiraService
 */
@Service
public class BusinessClientJiraServiceImpl {

    @Autowired
    private Environment env;

    public void getLsissuesRestTemplate() {
        String plainCreds = env.getProperty("JIRA_USERNAME") + ":" + env.getProperty("JIRA_PASSWORD");
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        // request url
        String url = "http://jira.unifin.com.mx:8080/rest/api/2/search?jql=worklogAuthor=jtoledano and worklogDate>='2022/05/02' and worklogDate<='2022/05/06'";

        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        // get JSON response
        String json = response.getBody();
        System.out.println(json);
    }

    private String getValueJsonChild(JSONObject jo, String f, String s) {
        JsonUtil<JSONObject, String, String, String> jf = (json, father, child) -> json != null
                ? (json.optJSONObject(father) != null ? json.optJSONObject(father).optString(child) : "")
                : "";
        return jf.apply(jo, f, s);
    }

    public List<IssueDTO> getLsIssues(Interval interval, String worklogAuthor, List<GroupDTO> lsGroup)
            throws Exception {

        String patternDate = "yyyy/MM/dd";
        String dtStartWeek = interval.getStart().toString(patternDate);
        String dtEndWeek = interval.getEnd().minusDays(1).toString(patternDate);
        Interval newInterval = new Interval(interval.getStart(), interval.getEnd().minusDays(1));

        StringBuilder urlSerchJira = new StringBuilder("http://jira.unifin.com.mx:8080/rest/api/2/search?jql=");
        urlSerchJira.append("worklogDate" + URLEncoder.encode(">=", "UTF-8") + "'" + dtStartWeek);
        urlSerchJira.append("'+and+worklogDate" + URLEncoder.encode("<=", "UTF-8") + "'" + dtEndWeek + "'");
        if (worklogAuthor != null && worklogAuthor != "")
            urlSerchJira.append("+and+worklogAuthor=" + worklogAuthor);
        HttpResponse<JsonNode> response = Unirest
                .get(urlSerchJira.toString())
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asJson();

        JSONObject body = response.getBody().getObject();
        JSONArray issueArray = body.optJSONArray("issues");
        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < issueArray.length(); i++) {
            JSONObject issue = issueArray.getJSONObject(i);
            lsIssue.addAll(this.getWorklog(newInterval, issue, lsGroup));
        }
        return lsIssue;
    }

    public List<IssueDTO> getWorklog(Interval interval, JSONObject issue, List<GroupDTO> lsGroup)
            throws Exception {
        JSONObject fields = issue.optJSONObject("fields");
        String id = issue.optString("id");
        HttpResponse<JsonNode> response = Unirest
                .get("http://jira.unifin.com.mx:8080/rest/api/2/issue/" + id + "/worklog")
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asJson();
        JSONObject body = response.getBody().getObject();
        JSONArray worklogs = body.optJSONArray("worklogs");

        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < worklogs.length(); i++) {

            String author = worklogs.getJSONObject(i).getJSONObject("author").getString("name");
            String updateAuthor = worklogs.getJSONObject(i).getJSONObject("updateAuthor").getString("name");

            Set<String> groupUser = lsGroup.stream()
                    .map(GroupDTO::getName)
                    .collect(Collectors.toSet());

            String dStarted = new String(worklogs.getJSONObject(i).getString("started"));
            DateTime dtStartedWl = new DateTime(dStarted);
            if (interval.contains(dtStartedWl) && groupUser.stream().anyMatch(s -> s.equals(author))) {
                IssueDTO workLog = new IssueDTO();
                workLog.setPuntoshistoria(fields.optString("customfield_10106"));

                workLog.setId(id);
                workLog.setKey(issue.optString("key"));
                workLog.setProyecto(this.getValueJsonChild(fields, "project", "name"));
                System.out.println(fields.optString("summary"));
                workLog.setAsignacion(fields.optString("summary"));

                String name = new String(worklogs.getJSONObject(i).getJSONObject("author").getString("displayName"));
                String timeSpent = new String(worklogs.getJSONObject(i).getString("timeSpent"));
                String dCreated = new String(worklogs.getJSONObject(i).getString("created"));
                String commentWl = new String(worklogs.getJSONObject(i).getString("comment"));

                workLog.setFechatrabajo(dStarted);
                workLog.setRegistrador(name);
                workLog.setCommentWl(commentWl);

                BigDecimal totalHoras = getHoursIssue(timeSpent);

                workLog.setHorasTrabajadas(totalHoras.toString());
                workLog.setFecharegistro(dCreated);
                lsIssue.add(workLog);
            }

        }
        if (worklogs.length() == 0) {
            System.out.println("no worklogs issue id" + id);
        }

        return lsIssue;
    }

    public BigDecimal getHoursIssue(String timeSpent) {
        String[] harray = timeSpent.split(" ");
        BigDecimal totalHoras = BigDecimal.ZERO;
        for (int i = 0; i < harray.length; i++) {
            if (harray[i].contains("m")) {
                totalHoras = totalHoras.add(
                        new BigDecimal(harray[i].replace("m", "")).divide(new BigDecimal(60), 2, RoundingMode.HALF_UP));
            } else if (harray[i].contains("h")) {
                totalHoras = totalHoras.add(new BigDecimal(harray[i].replace("h", "")));
            } else if (harray[i].contains("d")) {
                BigDecimal days = new BigDecimal(harray[i].replace("d", ""));
                totalHoras = totalHoras.add(days.multiply(new BigDecimal(8)));
            } else if (harray[i].contains("w")) {
                BigDecimal weeks = new BigDecimal(harray[i].replace("w", ""));
                totalHoras = totalHoras.add(weeks.multiply(new BigDecimal(40)));
            } else {
                totalHoras = totalHoras.add(BigDecimal.ZERO);
            }
        }
        return totalHoras;
    }

    public void getUserinfo(String userName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get("http://jira.unifin.com.mx:8080/rest/api/2/user")
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .queryString("username ", "jtoledano")
                .asJson();

        JSONObject body = response.getBody().getObject();
        JSONArray worklogs = body.optJSONArray("worklogs");

        GroupDTO groupDTO = new GroupDTO();
        String name = body.getString("name");
        String key = body.getString("key");
        String emailAddress = body.getString("emailAddress");
        String displayName = body.getString("displayName");

        groupDTO.setName(name);
        groupDTO.setKey(key);
        groupDTO.setEmailAddress(emailAddress);
        groupDTO.setDisplayName(displayName);

        System.out.println(response.getBody().getObject());
    }

    public void setPropertie() throws Exception {
        // The payload definition using the Jackson library
        JsonNodeFactory jnf = JsonNodeFactory.instance;
        ObjectNode payload = jnf.objectNode();
        {
        }

        // This code sample uses the 'Unirest' library:
        // http://unirest.io/java.html
        HttpResponse<JsonNode> response = Unirest
                .put("http://jira.unifin.com.mx:8080/rest/api/2/user/properties/Consultoria=ACI_group")
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .queryString("accountId", "5b10ac8d82e05b22cc7d4ef5")
                .body(payload)
                .asJson();

        System.out.println(response.getBody());
    }

    public ArrayList<GroupDTO> getLsUserbyGroup(ConsultoriaEnum group) throws Exception {
        StringBuilder urlSerchJira = new StringBuilder(
                "http://jira.unifin.com.mx:8080/rest/api/2/group/member?groupname=");
        urlSerchJira.append(group.getGroup());
        urlSerchJira.append("&includeInactiveUsers=true");
        urlSerchJira.append("&maxResults=500");
        System.out.println(env.getProperty("JIRA_USERNAME") + ":" + env.getProperty("JIRA_PASSWORD"));
        HttpResponse<JsonNode> response = Unirest
                .get(urlSerchJira.toString())
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asJson();
        System.out.println("URL:" + urlSerchJira.toString());
        JSONObject body = response.getBody().getObject();

        JSONArray values = body.optJSONArray("values");

        ArrayList<GroupDTO> lsUserGroup = new ArrayList<GroupDTO>();
        for (int i = 0; i < values.length(); i++) {
            GroupDTO uDTO = new GroupDTO();
            String name = values.getJSONObject(i).getString("name");
            String key = values.getJSONObject(i).getString("key");
            String emailAddress = values.getJSONObject(i).getString("emailAddress");
            String displayName = values.getJSONObject(i).getString("displayName");
            String isActive = values.getJSONObject(i).getString("active");

            uDTO.setName(name);
            uDTO.setKey(key);
            uDTO.setEmailAddress(emailAddress);
            uDTO.setDisplayName(displayName);
            uDTO.setGroup(group.getGroup());
            uDTO.setIsActive(Boolean.valueOf(isActive));
            lsUserGroup.add(uDTO);
        }
        System.out.println("USUARIOS EN EL GRUPO:" + lsUserGroup.size());
        return lsUserGroup;
    }

    public GroupResponse getLsUserbyGroupAsObject(ConsultoriaEnum group) throws Exception {
        StringBuilder urlSerchJira = new StringBuilder(
                "http://jira.unifin.com.mx:8080/rest/api/2/group/member?groupname=");
        urlSerchJira.append(group.getGroup());
        System.out.println(env.getProperty("JIRA_USERNAME") + ":" + env.getProperty("JIRA_PASSWORD"));
        GroupResponse response = Unirest
                .get(urlSerchJira.toString())
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asObject(GroupResponse.class)
                .getBody();

        response.getValues().stream().forEach(x -> System.out.println(x.getName()));

        return response;
    }

    public List<IssueDTO> getLsIssuesAsObject(Interval interval, GroupDTO g)
            throws Exception {

        String patternDate = "yyyy/MM/dd";
        String dtStartWeek = interval.getStart().toString(patternDate);
        String dtEndWeek = interval.getEnd().toString(patternDate);

        StringBuilder urlSerchJira = new StringBuilder("http://jira.unifin.com.mx:8080/rest/api/2/search?jql=");
        urlSerchJira.append("worklogDate" + URLEncoder.encode(">=", "UTF-8") + "'" + dtStartWeek);
        urlSerchJira.append("'+and+worklogDate" + URLEncoder.encode("<", "UTF-8") + "'" + dtEndWeek + "'");
        String worklogAuthor = g.getName().trim();
        if (worklogAuthor != null && worklogAuthor != "")
            urlSerchJira.append("+and+worklogAuthor=" + worklogAuthor);
        System.out.println("URL" + urlSerchJira.toString());
        SearchIssueResponse response = Unirest
                .get(urlSerchJira.toString())
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asObject(SearchIssueResponse.class)
                .getBody();

        List<IssueDTO> lsUserGroup = new ArrayList<IssueDTO>();
        response.getIssues().stream()
                .forEach(x -> {
                    // System.out.println(x.getId() + "-" + x.getFields().getProject().getName());
                    WorklogResponse responseWl = this.getWorklogAsObject(x.getId());
                    responseWl.getWorklogs().stream()
                            .forEach(y -> {
                                IssueDTO workLog = new IssueDTO();
                                workLog.setAsignacion(x.getFields().getSummary());
                                workLog.setPuntoshistoria(String.valueOf(x.getFields().getCustomfield_10106()));
                                workLog.setProyecto(x.getFields().getProject().getName());
                                workLog.setId(x.getId());
                                workLog.setKey(x.getKey());
                                workLog.setRegistrador(y.getAuthor().getDisplayName()+(g.getIsActive() ? "(Activo)" : "(Inactivo)"));
                                workLog.setName(y.getAuthor().getName());
                                workLog.setFechatrabajo(y.getStarted().toString());
                                workLog.setFecharegistro(y.getCreated().toString());
                                workLog.setHorasTrabajadas(getHoursIssue(y.getTimeSpent()).toString());
                                workLog.setCommentWl(y.getComment());
                                lsUserGroup.add(workLog);
                            });
                });

        // Set<String> lsName =
        // lsGroup.stream().map(GroupDTO::getName).collect(Collectors.toSet());

        List<IssueDTO> lsFilter = lsUserGroup.stream()
                // .filter(z -> lsName.stream().anyMatch(s ->
                // s.trim().equals(z.getName().trim())))
                // .peek(p->{
                // System.out.println(p.getName());
                // System.out.println(interval);
                // System.out.println(new DateTime(p.getFechatrabajo()));
                // System.out.println(interval.contains(new DateTime(p.getFechatrabajo())));
                // })
                .filter(i -> i.getName().trim().equals(worklogAuthor.trim()))
                .filter(z -> interval.contains(new DateTime(z.getFechatrabajo())))
                .collect(Collectors.toList());

        // lsFilter.stream().forEach(u -> System.out
        // .println(u.getKey() + "+" + u.getName() + "+" + u.getAsignacion() + "+" +
        // u.getFechatrabajo()));
        System.out.println("TOTAL DE TAREAS:" + lsFilter.size());
        return lsFilter;
    }

    public WorklogResponse getWorklogAsObject(String id) {
        WorklogResponse response = Unirest
                .get("http://jira.unifin.com.mx:8080/rest/api/2/issue/" + id + "/worklog")
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASSWORD"))
                .header("Accept", "application/json")
                .asObject(WorklogResponse.class)
                .getBody();

        // response.getWorklogs().stream()
        // .forEach(x -> System.out.println(x.getIssueId() + "-" + x.getUpdated() + "-"
        // + x.getAuthor().getName()));

        return response;

    }
}