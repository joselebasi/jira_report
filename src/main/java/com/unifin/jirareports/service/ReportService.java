package com.unifin.jirareports.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.jira.GroupEnum;
import com.unifin.jirareports.model.jira.IssueDTO;
import com.unifin.jirareports.model.jira.WorklogAuthorDTO;
import com.unifin.jirareports.model.jira.GroupDTO;

import org.joda.time.DateTime;
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
			List<GroupDTO> lsU = clientJira.getLsUserbyGroup(g);
			System.out.println(g.getGroup());
			List<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (GroupDTO u : lsU) {
				resultado.addAll(jiraService.getLsIssueByDate(i, u.getName().trim()));
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

	public void sendReportsConsultoria(List<ConsultoraDTO> lsConsultora) throws Exception {
		DateTime dt = new DateTime();
		Interval i = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		String dtStartWeek = i.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = i.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

		for (ConsultoraDTO c : lsConsultora) {
			List<GroupDTO> lsU = clientJira.getLsUserbyGroup(c.getConsultora());
			System.out.println(c.getConsultora().getGroup());
			ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (GroupDTO u : lsU) {
				resultado.addAll(jiraService.getLsIssueByDate(i, u.getName().trim()));
			}
			System.out.println("total" + resultado.size());
			// StringWriter fw = csvFileService.writeCSVFile(resultado);
			// ByteArrayResource attachmentCsv = new
			// ByteArrayResource(fw.getBuffer().toString().getBytes());

			ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", resultado);
			emailService.sendEmailWithAttachment(c.getLsEmail(), "Weekly report",
					c.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek, attachmentExcel);
		}
	}

	public void sendReportConsultoriaDate(ConsultoraDTO dto) throws Exception {
		DateTime start = new DateTime(dto.getStartInterval().toString());
		DateTime end = new DateTime(dto.getEndInterval().toString());
		Interval iCustom = new Interval(start, end);

		String dtStartWeek = iCustom.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = iCustom.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);

		List<GroupDTO> lsU = clientJira.getLsUserbyGroup(dto.getConsultora());
		System.out.println(dto.getConsultora().getGroup());
		ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
		for (GroupDTO u : lsU) {
			resultado.addAll(jiraService.getLsIssueByDate(iCustom, u.getName().trim()));
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
		String dtEndWeek = iCustom.getEnd().minusDays(1).toString("yyyy/MM/dd");
		System.out.println("dtStartWeek " + dtStartWeek + " dtEndWeek " + dtEndWeek);


		ArrayList<IssueDTO> lsU = jiraService.getLsIssueByDate(iCustom, dto.getWorklogAuthor());

		System.out.println("total " + lsU.size());
		// StringWriter fw = csvFileService.writeCSVFile(resultado);
		// ByteArrayResource attachmentCsv = new
		// ByteArrayResource(fw.getBuffer().toString().getBytes());

		ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", lsU);
		emailService.sendEmailWithAttachment(dto.getLsEmail(), "Weekly report",
				dto.getWorklogAuthor() + "_" + dtStartWeek + "_" + dtEndWeek, attachmentExcel);

	}
}