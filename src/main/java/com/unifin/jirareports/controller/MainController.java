package com.unifin.jirareports.controller;

import com.unifin.jirareports.model.rest.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private Environment env;

    @GetMapping("/")
    Result getVersion() {
        return new Result("Version:1.0");
    }

   

}
