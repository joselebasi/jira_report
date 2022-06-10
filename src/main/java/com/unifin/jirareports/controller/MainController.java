package com.unifin.jirareports.controller;

import java.util.Arrays;
import java.util.List;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.GroupDTO;
import com.unifin.jirareports.model.rest.Result;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    BusinessClientJiraServiceImpl client;

    @GetMapping("/")
    Result getVersion() {
        return new Result("Version:2.0");
    }

    @GetMapping("/test")
    Result getTest() {
        try {
            //client.getLsUserbyGroupAsObject(GroupEnum.ACI_GROUP);

            String dtStartWeek = "2022-05-16";//i.getStart().toString("yyyy/MM/dd");
            String dtEndWeek =  "2022-05-22"; //i.getEnd().minusDays(1).toString("yyyy/MM/dd");         
            DateTime start = new DateTime(dtStartWeek);
		    DateTime end = new DateTime(dtEndWeek);
		    Interval iCustom = new Interval(start, end);

            System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

            List<GroupDTO> lsUser = Arrays.asList(
                    new GroupDTO("carlos.moya", "", "", "", ""));

            client.getLsIssuesAsObject(iCustom, "carlos.moya", lsUser);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Result("Version:1.0");
    }

}
