package com.unifin.jirareports.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.unifin.jirareports.business.jira.BusinessClientJiraServiceImpl;
import com.unifin.jirareports.model.jira.ConsultoraDTO;
import com.unifin.jirareports.model.jira.ConsultoraSchedulerDTO;
import com.unifin.jirareports.model.jira.ConsultoriaEnum;
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

		for (ConsultoriaEnum g : ConsultoriaEnum.values()) {
			ArrayList<GroupDTO> lsUser = clientJira.getLsUserbyGroup(g);
			System.out.println(g.getGroup());
			ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
			for (GroupDTO u : lsUser) {
				resultado.addAll(jiraService.getLsIssueByDate(i, u));
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

	public void sendWeeklyReportsConsultoria(List<ConsultoraSchedulerDTO> lsConsultora) throws Exception {
		DateTime dt = new DateTime();
		Interval lastWeekInterval = dt.minusWeeks(1).weekOfWeekyear().toInterval();
		Interval newWeekInterval = new Interval(lastWeekInterval.getStart(), lastWeekInterval.getEnd().minusDays(1));
		System.out.println(newWeekInterval.toString());
		for (ConsultoraSchedulerDTO c : lsConsultora) {
			String dtStartWeek = newWeekInterval.getStart().toString("yyyy/MM/dd");
			String dtEndWeek = newWeekInterval.getEnd().toString("yyyy/MM/dd");
			String titleEmail = c.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek;
			this.createReportSendEmail(c.getConsultora(), newWeekInterval, c.getLsEmail(), titleEmail,
					"Reporte semanal de tareas de jira");
		}
	}

	public void sendMonthlyReportsConsultoria(List<ConsultoraSchedulerDTO> lsConsultora) throws Exception {
		DateTime dt = new DateTime();
		Interval monthlyInterval = dt.minusMonths(1).monthOfYear().toInterval();
		Interval newWeekInterval = new Interval(monthlyInterval.getStart(), monthlyInterval.getEnd().minusDays(1));
		System.out.println(newWeekInterval.toString());
		for (ConsultoraSchedulerDTO c : lsConsultora) {
			String dtStartWeek = newWeekInterval.getStart().toString("yyyy/MM/dd");
			String dtEndWeek = newWeekInterval.getEnd().toString("yyyy/MM/dd");
			String titleEmail = c.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek;
			this.createReportSendEmail(c.getConsultora(), newWeekInterval, c.getLsEmail(), titleEmail,
					"Reporte mensual de tareas de jira");
		}
	}

	public void sendDailyReportsConsultoria(List<ConsultoraSchedulerDTO> lsConsultora) throws Exception {
		DateTime dt = new DateTime().withTimeAtStartOfDay();
		Interval newWeekInterval = new Interval(dt.minusDays(1), dt);
		System.out.println(newWeekInterval.toString());
		for (ConsultoraSchedulerDTO c : lsConsultora) {
			String dtStartWeek = newWeekInterval.getStart().toString("yyyy/MM/dd");
			String titleEmail = c.getConsultora().getGroup() + "_" + dtStartWeek;
			this.createReportSendEmail(c.getConsultora(), newWeekInterval, c.getLsEmail(), titleEmail,
					"Reporte diario de tareas de jira");
		}
	}

	public void sendReportConsultoriaDate(ConsultoraDTO dto) throws Exception {
		DateTime start = new DateTime(dto.getStartInterval().toString()).withTimeAtStartOfDay();
		DateTime end = new DateTime(dto.getEndInterval().toString());
		DateTime endFinalOfDay = new DateTime(end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), 23, 59, 59);
		Interval iCustom = new Interval(start, endFinalOfDay);
		String dtStartWeek = iCustom.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = iCustom.getEnd().toString("yyyy/MM/dd");
		iCustom = new Interval(iCustom.getStart(), iCustom.getEnd().plusDays(1));
		System.out.println("Interval " + iCustom.toString());
		String titleEmail = dto.getConsultora().getGroup() + "_" + dtStartWeek + "_" + dtEndWeek;
		this.createReportSendEmail(dto.getConsultora(), iCustom, dto.getLsEmail(), titleEmail,
				"Reporte configurable por consultoria");
	}

	
	public void sendReportConsultorDate(WorklogAuthorDTO dto) throws Exception {
		DateTime start = new DateTime(dto.getStartInterval().toString()).withTimeAtStartOfDay();
		DateTime end = new DateTime(dto.getEndInterval().toString());
		DateTime endFinalOfDay = new DateTime(end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), 23, 59, 59);
		Interval iCustom = new Interval(start, endFinalOfDay);

		String dtStartWeek = iCustom.getStart().toString("yyyy/MM/dd");
		String dtEndWeek = iCustom.getEnd().toString("yyyy/MM/dd");

		iCustom = new Interval(iCustom.getStart(), iCustom.getEnd().plusDays(1));

		System.out.println("Interval " + iCustom.toString());

		GroupDTO consultor = new GroupDTO(dto.getWorklogAuthor().trim(), "", "", "", "",true);

		List<IssueDTO> lsU = jiraService.getLsIssueByDate(iCustom, consultor);

		System.out.println("total: " + lsU.size());
		// StringWriter fw = csvFileService.writeCSVFile(resultado);
		// ByteArrayResource attachmentCsv = new
		// ByteArrayResource(fw.getBuffer().toString().getBytes());

		ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", lsU);
		emailService.sendEmailWithAttachment(dto.getLsEmail(), "Reporte configurable por consultor",
				dto.getWorklogAuthor() + "_" + dtStartWeek + "_" + dtEndWeek, attachmentExcel);

	}

	public void createReportSendEmail(ConsultoriaEnum c, Interval interval, String[] arrayToEmail, String subjectEmail,
			String bodyEmail)
			throws Exception {
		ArrayList<GroupDTO> lsUser = clientJira.getLsUserbyGroup(c);
		System.out.println(c.getGroup());
		ArrayList<IssueDTO> resultado = new ArrayList<IssueDTO>();
		for (GroupDTO g : lsUser) {
			resultado.addAll(jiraService.getLsIssueByDate(interval, g));
		}
		System.out.println("total " + resultado.size());
		List<IssueDTO> orderResult = resultado.stream()
				.sorted((o1, o2) -> o1.getFechatrabajo().compareTo(o2.getFechatrabajo())).collect(Collectors.toList());
		// StringWriter fw = csvFileService.writeCSVFile(resultado);
		// ByteArrayResource attachmentCsv = new
		// ByteArrayResource(fw.getBuffer().toString().getBytes());
		ByteArrayResource attachmentExcel = excelService.writeExcel("reporte", orderResult);
		emailService.sendEmailWithAttachment(arrayToEmail, bodyEmail,
				subjectEmail, attachmentExcel);
	}

}