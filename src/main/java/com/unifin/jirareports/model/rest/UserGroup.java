package com.unifin.jirareports.model.rest;

import lombok.Data;

@Data
public class UserGroup {
    public String self;
    public String name;
    public String key;
    public String emailAddress;
    public String displayName;
    public boolean active;
    public String timeZone;
    
}
