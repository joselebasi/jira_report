package com.unifin.jirareports.model.rest;

import lombok.Data;

@Data
public class Issue {
    private String expand;
    private String id;
    private String self;
    private String key;
    private Fields fields;

}