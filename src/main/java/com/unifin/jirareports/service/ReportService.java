package com.unifin.jirareports.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.UserGroupDTO;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("reportService")
public class ReportService {

    @Autowired
	BusinessClientJiraServiceImpl clientJira;

	@Autowired
	JiraService jiraService;

	@Autowired
	EmailService emailService;

	@Autowired
	CSVFileService csvFileService;

    public void sendReportsConsultoria() throws Exception{
        DateTime dt = new DateTime();
		Interval i = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		String dtStartWeek = i.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = i.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

		for (GroupEnum g : GroupEnum.values()) {
			List<UserGroupDTO> lsU = clientJira.getLsUserbyGroup(g);
			System.out.println(g.getGroup());
			List<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (UserGroupDTO u : lsU) {				
				resultado.addAll(jiraService.getLsIssueByDate(i, u.getName().trim()));
			}
			System.out.println("total" + resultado.size());
			StringWriter fw = csvFileService.writeCSVFile(resultado);

			String[] emails = new String[] { "jjtoledanomorales@gmail.com" };
			//String[] emails = new String[] { "lparra@unifin.com.mx", "jose.espino_ext@unifin.com.mx", "jjtoledanomorales@gmail.com" };
			
			emailService.sendEmailWithAttachment(emails, "Weekly report",
					g.getGroup() + "_" + dtStartWeek + "_" + dtEndWeek, fw);

		}
    }

	public void sendReportsConsultoria(List<ConsultoraDTO> lsConsultora) throws Exception{
        DateTime dt = new DateTime();
		Interval i = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		String dtStartWeek = i.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = i.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

		for (ConsultoraDTO c : lsConsultora) {
			List<UserGroupDTO> lsU = clientJira.getLsUserbyGroup(c.getConsultora());
			System.out.println(c.getConsultora().getGroup());
			List<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (UserGroupDTO u : lsU) {				
				resultado.addAll(jiraService.getLsIssueByDate(i, u.getName().trim()));
			}
			System.out.println("total" + resultado.size());
			StringWriter fw = csvFileService.writeCSVFile(resultado);
			emailService.sendEmailWithAttachment(c.getLsEmail(), "Weekly report",
					c.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek, fw);

		}
    }
    
}