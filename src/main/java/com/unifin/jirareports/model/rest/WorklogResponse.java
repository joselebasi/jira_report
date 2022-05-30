package com.unifin.jirareports.model.rest;

import java.util.ArrayList;

import lombok.Data;

@Data
public class WorklogResponse {

    private int startAt;
    private int maxResults;
    private int total;
    private ArrayList<Worklog> worklogs;
    
}
