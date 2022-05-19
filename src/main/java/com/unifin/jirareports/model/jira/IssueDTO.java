package com.unifin.jirareports.model.jira;

import lombok.Data;

@Data
public class IssueDTO {

    private String id;
    private String key;
    private String asignacion;//summary;
    private String registrador;//name;
    private String horasTrabajadas;//timeSpent;
    private String fecharegistro;//dCreated;
    private String fechatrabajo;//dStarted;
    private String proyecto; //nameProject;
    private String commentWl;
    private String puntoshistoria;//historyPoints;

}
