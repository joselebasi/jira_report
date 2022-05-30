package com.unifin.jirareports.model.rest;

import java.sql.Date;

import lombok.Data;

@Data
public class Worklog {

    private String self;
    private Author author;
    private UpdateAuthor updateAuthor;
    private String comment;
    private Date created;
    private Date updated;
    private Date started;
    private String timeSpent;
    private int timeSpentSeconds;
    private String id;
    private String issueId;
    
}
