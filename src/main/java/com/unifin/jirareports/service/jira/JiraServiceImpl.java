package com.unifin.jirareports.service.jira;

import java.util.ArrayList;
import java.util.List;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.GroupDTO;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.service.JiraService;

import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JiraServiceImpl implements JiraService{

    @Autowired BusinessClientJiraServiceImpl clientJira;
    

    @Override
    public List<IssueDTO> getLsIssueByUser(String user) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IssueDTO> getLsIssueByDate(Interval interval, String worklogAuthor, List<GroupDTO> lsUser) throws Exception{
        return clientJira.getLsIssues(interval, worklogAuthor, lsUser);
    }

   
}