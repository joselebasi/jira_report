package com.unifin.jirareports.controller;

import com.unifin.jirareports.model.rest.Result;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    Result getVersion() {
        return new Result("Version:1.0");
    }

   

}
