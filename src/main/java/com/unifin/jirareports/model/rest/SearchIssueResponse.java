package com.unifin.jirareports.model.rest;

import java.util.ArrayList;

import lombok.Data;

@Data
public class SearchIssueResponse {
    public String expand;
    public int startAt;
    public int maxResults;
    public int total;
    public ArrayList<Issue> issues;
    
}
