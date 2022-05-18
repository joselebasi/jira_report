package com.unifin.jirareports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.UserGroupDTO;
import com.unifin.jirareports.service.CSVFileService;
import com.unifin.jirareports.service.EmailService;
import com.unifin.jirareports.service.JiraService;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JirareportsApplication{

	public static void main(String[] args) {
		SpringApplication.run(JirareportsApplication.class, args);
	}

}
