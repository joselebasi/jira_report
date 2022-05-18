package com.unifin.jirareports.service;

import java.util.List;

import com.unifin.jirareports.model.jira.IssueDTO;

import org.joda.time.Interval;

public interface JiraService {

   List<IssueDTO> getLsIssueByUser(String user);
   List<IssueDTO> getLsIssueByDate(Interval interval, String worklogAuthor) throws Exception;
   

}
