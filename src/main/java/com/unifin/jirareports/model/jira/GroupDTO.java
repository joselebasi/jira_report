package com.unifin.jirareports.model.jira;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String name;
    private String key;
    private String emailAddress;
    private String displayName;
    private String group;
}
