package com.unifin.jirareports.business.jira;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.UserGroupDTO;
import com.unifin.jirareports.service.util.JsonUtil;

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
        String plainCreds = env.getProperty("JIRA_USERNAME")+":"+env.getProperty("JIRA_PASWORD");
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

    interface JsonFuntion<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    private String getValueJsonChild(JSONObject jo, String f, String s) {
        JsonUtil<JSONObject, String, String, String> jf = (json, father, child) -> json != null
                ? (json.optJSONObject(father) != null ? json.optJSONObject(father).optString(child) : "")
                : "";
        return jf.apply(jo, f, s);
    }

    public List<IssueDTO> getLsIssues(Interval interval, String worklogAuthor) throws Exception {

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
                
                
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASWORD"))
                .header("Accept", "application/json")
                .asJson();

               
        System.out.println(response.getBody().toPrettyString()); 

        JSONObject body = response.getBody().getObject();
        JSONArray issues = body.optJSONArray("issues");
        issues.forEach(System.out::println);

        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < issues.length(); i++) {

            JSONObject fields = issues.getJSONObject(i).optJSONObject("fields");

            lsIssue.addAll(this.getWorklog(newInterval, fields));
        }

        return lsIssue;
    }

    public List<IssueDTO> getWorklog(Interval interval, JSONObject issues) throws Exception {

        String id = issues.optString("id");
        HttpResponse<JsonNode> response = Unirest
                .get("http://jira.unifin.com.mx:8080/rest/api/2/issue/" + id + "/worklog")
                .basicAuth(env.getProperty("JIRA_USERNAME"), env.getProperty("JIRA_PASWORD"))
                .header("Accept", "application/json")
                .asJson();
        JSONObject body = response.getBody().getObject();
        JSONArray worklogs = body.optJSONArray("worklogs");
        JSONObject fields = issues.optJSONObject("fields");

        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < worklogs.length(); i++) {
            String dStarted = new String(worklogs.getJSONObject(i).getString("started"));
            DateTime dtStartedWl = new DateTime(dStarted);
            if (interval.contains(dtStartedWl)) {
                IssueDTO workLog = new IssueDTO();
                workLog.setHistoryPoints(fields.optString("customfield_10106"));
                workLog.setId(id);
                workLog.setKey(issues.optString("key"));
                workLog.setNameProject(this.getValueJsonChild(fields, "project", "name"));
                workLog.setSummary(fields.optString("summary"));

                String name = new String(worklogs.getJSONObject(i).getJSONObject("author").getString("displayName"));
                String timeSpent = new String(worklogs.getJSONObject(i).getString("timeSpent"));
                String dCreated = new String(worklogs.getJSONObject(i).getString("created"));
                String commentWl = new String(worklogs.getJSONObject(i).getString("comment"));
                // System.out.println("name" + "-" + name + "-" + "timeSpent" + "-" +
                // timeSpent);

                workLog.setDStarted(dStarted);
                workLog.setName(name);
                workLog.setCommentWl(commentWl);
                workLog.setTimeSpent(timeSpent);
                workLog.setdCreated(dCreated);
                lsIssue.add(workLog);
            }

        }
        if (worklogs.length() == 0) {
            System.out.println("no worklogs issue id" + id);
        }

        return lsIssue;
    }

    public void getUserinfo(String userName) throws Exception {
        HttpResponse<JsonNode> response = Unirest.get("http://jira.unifin.com.mx:8080/rest/api/2/user")
                .basicAuth("jtoledano", "Zmxn1029")
                .header("Accept", "application/json")
                .queryString("username ", "jtoledano")
                .asJson();

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
                .basicAuth("jtoledano", "Zmxn1029")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .queryString("accountId", "5b10ac8d82e05b22cc7d4ef5")
                .body(payload)
                .asJson();

        System.out.println(response.getBody());
    }

    public List<UserGroupDTO> getLsUserbyGroup(GroupEnum group) throws Exception {
        StringBuilder urlSerchJira = new StringBuilder(
                "http://jira.unifin.com.mx:8080/rest/api/2/group/member?groupname=");
        urlSerchJira.append(group.getGroup());

        HttpResponse<JsonNode> response = Unirest
                .get(urlSerchJira.toString())
                .basicAuth("jtoledano", "Zmxn1029")
                .header("Accept", "application/json")
                .asJson();
        JSONObject body = response.getBody().getObject();
        JSONArray values = body.optJSONArray("values");

        List<UserGroupDTO> lsUserGroup = new ArrayList<UserGroupDTO>();
        for (int i = 0; i < values.length(); i++) {
            UserGroupDTO uDTO = new UserGroupDTO();
            String name = values.getJSONObject(i).getString("name");
            String key = values.getJSONObject(i).getString("key");
            String emailAddress = values.getJSONObject(i).getString("emailAddress");
            String displayName = values.getJSONObject(i).getString("displayName");

            uDTO.setName(name);
            uDTO.setKey(key);
            uDTO.setEmailAddress(emailAddress);
            uDTO.setDisplayName(displayName);
            uDTO.setGroup(group.getGroup());
            lsUserGroup.add(uDTO);
        }

        return lsUserGroup;
    }
}