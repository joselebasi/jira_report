package com.unifin.jirareports.controller;

import com.unifin.jirareports.model.rest.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private Environment env;

    @PostMapping("/")
    Result getVersion() {
        return new Result("Version:1.0"+env.getProperty("MAIL_USERNAME"));
    }

   

}
