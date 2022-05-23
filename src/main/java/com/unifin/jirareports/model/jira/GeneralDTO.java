package com.unifin.jirareports.model.jira;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class GeneralDTO{

    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate startInterval;
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate endInterval;
    private String[] lsEmail;
    
}
