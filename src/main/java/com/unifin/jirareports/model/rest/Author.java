package com.unifin.jirareports.model.rest;

import lombok.Data;

@Data
public class Author {

    private String self;
    private String name;
    private String key;
    private String emailAddress;
    private String displayName;
    private boolean active;
    private String timeZone;
    
}
