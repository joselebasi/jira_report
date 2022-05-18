package com.unifin.jirareports.business.jira;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.UserGroupDTO;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * BusinessClientJiraService
 */
@Service
public class BusinessClientJiraServiceImpl {

    public void getLsissuesRestTemplate() {
        String plainCreds = "jtoledano:Zmxn1029";
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

    public List<IssueDTO> getLsIssues(Interval interval, String worklogAuthor) throws Exception {
        // This code sample uses the 'Unirest' library:
        // http://unirest.io/java.html

        String patternDate = "yyyy/MM/dd";
        String dtStartWeek = interval.getStart().toString(patternDate);
        String dtEndWeek = interval.getEnd().minusDays(1).toString(patternDate);
        Interval newInterval = new Interval(interval.getStart(), interval.getEnd().minusDays(1));

        StringBuilder urlSerchJira = new StringBuilder("http://jira.unifin.com.mx:8080/rest/api/2/search?jql=");
        urlSerchJira.append("worklogDate" + URLEncoder.encode(">=", "UTF-8") + "'" + dtStartWeek);
        urlSerchJira.append("'+and+worklogDate" + URLEncoder.encode("<=", "UTF-8") + "'" + dtEndWeek + "'");
        if (worklogAuthor != null && worklogAuthor != "")
            urlSerchJira.append("+and+worklogAuthor=" + worklogAuthor);
        //System.out.println(urlSerchJira.toString());
        HttpResponse<JsonNode> response = Unirest
                // .get("http://jira.unifin.com.mx:8080/rest/api/2/search?jql=assignee=jtoledano")}
                // .get("http://jira.unifin.com.mx:8080/rest/api/2/search?jql=worklogDate='2022/05/09'")
                .get(urlSerchJira.toString())
                .basicAuth("jtoledano", "Zmxn1029")
                .header("Accept", "application/json")
                // .queryString("query", "query")
                // .queryString("projectKeys", "{projectKeys}")
                .asJson();

        JSONObject body = response.getBody().getObject();
        JSONArray issues = body.optJSONArray("issues");

        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < issues.length(); i++) {
            //customfield_10106
            IssueDTO issue = new IssueDTO();
            String nameProject = new String(issues.getJSONObject(i).getJSONObject("fields").getJSONObject("project")
                    .getString("name"));
            // String assignee =
            // issues.getJSONObject(i).getJSONObject("fields").getJSONObject("assignee")
            // .getString("name");

            String summary = "";
            try {
                summary = new String(issues.getJSONObject(i).getJSONObject("fields").getString("summary"));
            } catch (JSONException je) {
                summary = "No summary";
            }

            String description = "";
            try {
                description = new String(issues.getJSONObject(i).getJSONObject("fields").getString("description"));
            } catch (JSONException je) {
                description = "No decription";
            }

            String historyPoints ="";
            try {
                historyPoints = new String(issues.getJSONObject(i).getJSONObject("fields").getString("customfield_10106"));
            } catch (JSONException je) {
                historyPoints = "0";
            }

            String id = new String(issues.getJSONObject(i).getString("id"));
            // String url = issues.getJSONObject(i).getString("self");
            String key =new String(issues.getJSONObject(i).getString("key"));

            issue.setHistoryPoints(historyPoints);
            issue.setId(id);
            issue.setKey(key);
            issue.setNameProject(nameProject);
            issue.setSummary(summary);

            // System.out.println(id + "-" + key + "-" + assignee + "-" + nameProject + "-"
            // + summary + "-"
            // + description + "-" + url);

            lsIssue.addAll(this.getWorklog(newInterval, issue));
        }

        return lsIssue;

        // .getJSONArray("fields")
        // System.out.println(response.getBody());
    }

    public List<IssueDTO> getWorklog(Interval interval, IssueDTO issueDTO) throws Exception {
        HttpResponse<JsonNode> response = Unirest
                .get("http://jira.unifin.com.mx:8080/rest/api/2/issue/" + issueDTO.getId() + "/worklog")
                .basicAuth("jtoledano", "Zmxn1029")
                .header("Accept", "application/json")
                // .queryString("query", "query")
                // .queryString("projectKeys", "{projectKeys}")
                .asJson();
        JSONObject body = response.getBody().getObject();
        JSONArray worklogs = body.optJSONArray("worklogs");

        List<IssueDTO> lsIssue = new ArrayList<IssueDTO>();
        for (int i = 0; i < worklogs.length(); i++) {
            String dStarted = new String(worklogs.getJSONObject(i).getString("started"));
            DateTime dtStartedWl = new DateTime(dStarted);
            if (interval.contains(dtStartedWl)) {
                IssueDTO workLog = new IssueDTO();
                workLog.setHistoryPoints(issueDTO.getHistoryPoints());
                workLog.setId(issueDTO.getId());
                workLog.setKey(issueDTO.getKey());
                workLog.setNameProject(issueDTO.getNameProject());
                workLog.setSummary(issueDTO.getSummary());
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
            System.out.println("no worklogs issue id" + issueDTO.getId());
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

        // Connect Jackson ObjectMapper to Unirest
        Unirest.setObjectMapper((com.mashape.unirest.http.ObjectMapper) new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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