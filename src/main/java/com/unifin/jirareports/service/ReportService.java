package com.unifin.jirareports.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.jira.ConsultoraWeetlyDTO;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.WorklogAuthorDTO;
import com.unifin.jirareports.model.jira.GroupDTO;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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

	@Autowired
	ExcelService excelService;

	public void sendReportsConsultoria() throws Exception {
		DateTime dt = new DateTime();
		Interval i = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		String dtStartWeek = i.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = i.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

		for (GroupEnum g : GroupEnum.values()) {
			ArrayList<GroupDTO> lsUser = clientJira.getLsUserbyGroup(g);
			System.out.println(g.getGroup());
			ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (GroupDTO u : lsUser) {
				resultado.addAll(jiraService.getLsIssueByDate(i, u.getName().trim(), lsUser));
			}
			System.out.println("total" + resultado.size());
			StringWriter fw = csvFileService.writeCSVFile(resultado);

			String[] emails = new String[] { "jjtoledanomorales@gmail.com" };
			// String[] emails = new String[] { "lparra@unifin.com.mx",
			// "jose.espino_ext@unifin.com.mx", "jjtoledanomorales@gmail.com" };

			// emailService.sendEmailWithAttachment(emails, "Weekly report",
			// g.getGroup() + "_" + dtStartWeek + "_" + dtEndWeek, fw);

		}
	}

	public void sendReportsConsultoria(List<ConsultoraWeetlyDTO> lsConsultora) throws Exception {
		DateTime dt = new DateTime();
		Interval lastWeekInterval = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		Interval newWeekInterval = new Interval(lastWeekInterval.getStart(), lastWeekInterval.getEnd().minusDays(1));
		System.out.println(newWeekInterval.toString());
		for (ConsultoraWeetlyDTO c : lsConsultora) {
			ArrayList<GroupDTO> lsUser = clientJira.getLsUserbyGroup(c.getConsultora());
			System.out.println(c.getConsultora().getGroup());
			ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (GroupDTO u : lsUser) {
				resultado.addAll(jiraService.getLsIssueByDate(newWeekInterval, u.getName().trim(), lsUser));
			}
			System.out.println("total" + resultado.size());
			// StringWriter fw = csvFileService.writeCSVFile(resultado);
			// ByteArrayResource attachmentCsv = new
			// ByteArrayResource(fw.getBuffer().toString().getBytes());

			ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", resultado);
			emailService.sendEmailWithAttachment(c.getLsEmail(), "Weekly report",
					c.getConsultora().getGroup() + "_" + newWeekInterval.getStart().toString("yyyy/MM/dd") + "_"
							+ newWeekInterval.getEnd().toString("yyyy/MM/dd"),
					attachmentExcel);
		}
	}

	public void sendReportConsultoriaDate(ConsultoraDTO dto) throws Exception {
		DateTime start = new DateTime(dto.getStartInterval().toString());
		DateTime end = new DateTime(dto.getEndInterval().toString());
		Interval iCustom = new Interval(start, end);

		String dtStartWeek = iCustom.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = iCustom.getEnd().toString("yyyy/MM/dd");

		System.out.println("Interval " + iCustom.toString());

		ArrayList<GroupDTO> lsUser = clientJira.getLsUserbyGroup(dto.getConsultora());
		System.out.println(dto.getConsultora().getGroup());
		ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
		for (GroupDTO u : lsUser) {
			resultado.addAll(jiraService.getLsIssueByDate(iCustom, u.getName().trim(), lsUser));
		}
		System.out.println("total " + resultado.size());
		// StringWriter fw = csvFileService.writeCSVFile(resultado);
		// ByteArrayResource attachmentCsv = new
		// ByteArrayResource(fw.getBuffer().toString().getBytes());

		ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", resultado);
		emailService.sendEmailWithAttachment(dto.getLsEmail(), "Weekly report",
				dto.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek, attachmentExcel);

	}

	public void sendReportConsultorDate(WorklogAuthorDTO dto) throws Exception {
		DateTime start = new DateTime(dto.getStartInterval().toString());
		DateTime end = new DateTime(dto.getEndInterval().toString());
		Interval iCustom = new Interval(start, end);

		String dtStartWeek = iCustom.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = iCustom.getEnd().toString("yyyy/MM/dd");

		if (Days.daysIn(iCustom).getDays() == 0) {
            iCustom = new Interval(iCustom.getStart(), iCustom.getEnd().plusDays(1));
        }

		System.out.println("Interval " + iCustom.toString());

		List<GroupDTO> lsUser = Arrays.asList(
				new GroupDTO(dto.getWorklogAuthor().trim(), "", "", "", ""));

		List<IssueDTO> lsU = jiraService.getLsIssueByDate(iCustom, dto.getWorklogAuthor(), lsUser);

		System.out.println("total: " + lsU.size());
		// StringWriter fw = csvFileService.writeCSVFile(resultado);
		// ByteArrayResource attachmentCsv = new
		// ByteArrayResource(fw.getBuffer().toString().getBytes());

		ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", lsU);
		emailService.sendEmailWithAttachment(dto.getLsEmail(), "Weekly report",
				dto.getWorklogAuthor() + "_" + dtStartWeek + "_" + dtEndWeek, attachmentExcel);

	}
}