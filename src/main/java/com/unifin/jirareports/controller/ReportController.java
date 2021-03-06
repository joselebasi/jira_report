package com.unifin.jirareports.controller;

import java.util.List;

import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.jira.ConsultoraSchedulerDTO;
import com.unifin.jirareports.model.jira.WorklogAuthorDTO;
import com.unifin.jirareports.model.rest.Result;
import com.unifin.jirareports.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/unifin/jirareport")
public class ReportController {

    @Autowired ReportService reportService;

    //@Operation(summary = "Send email by consultancy")
    //@PostMapping("/consultorias")
    Result sendReportConsultancy() {
        try{
            reportService.sendReportsConsultoria();
        }catch(Exception e){
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }
    
    @Operation(summary = "Envio de reporte semanal a las consultoras de la semana anterior inmediata")
    @PostMapping("/semanal")
    Result sendReportConsultancyWeekly(@RequestBody List<ConsultoraSchedulerDTO> lsConsultora) {
        try{
            reportService.sendWeeklyReportsConsultoria(lsConsultora);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }

    @Operation(summary = "Envio de reporte mensual a las consultoras del mes anterior inmediata")
    @PostMapping("/mensual")
    Result sendReportConsultancyMonthly(@RequestBody List<ConsultoraSchedulerDTO> lsConsultora) {
        try{
            reportService.sendMonthlyReportsConsultoria(lsConsultora);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }

    @Operation(summary = "Envio de reporte diario a las consultoras del dia anterior inmediato")
    @PostMapping("/diario")
    Result sendReportConsultancyDaily(@RequestBody List<ConsultoraSchedulerDTO> lsConsultora) {
        try{
            reportService.sendDailyReportsConsultoria(lsConsultora);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }

    @Operation(summary = "Envio de reporte por consultora de un intevalo de fechas")
    @PostMapping("/consultora")
    Result sendReportCustom(@RequestBody ConsultoraDTO dto) {
        try{
            reportService.sendReportConsultoriaDate(dto);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reportes enviados correctamente");
    }

    @Operation(summary = "Envio de reporte por consultor de un intevalo de fechas")
    @PostMapping("/consultor")
    Result sendReportUser(@RequestBody WorklogAuthorDTO dto) {
        try{
            reportService.sendReportConsultorDate(dto);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new Result("Error: "+e.getMessage());
        }
        return new Result("Reporte enviado correctamente");
    }

}
