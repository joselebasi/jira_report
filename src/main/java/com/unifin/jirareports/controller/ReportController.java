package com.unifin.jirareports.controller;

import java.util.List;

import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.rest.Result;
import com.unifin.jirareports.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/unifin/jira/report")
public class ReportController {

    @Autowired ReportService reportService;

    @Operation(summary = "Send email by consultancy")
    @PostMapping("/consultorias")
    Result sendReportConsultancy() {
        try{
            reportService.sendReportsConsultoria();
        }catch(Exception e){
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }
    
    @Operation(summary = "Send email by consultancy list in request")
    @PostMapping("/consultorias/custom")
    Result sendReportConsultancyCustom(@RequestBody List<ConsultoraDTO> lsConsultora) {
        try{
            reportService.sendReportsConsultoria(lsConsultora);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }

}
