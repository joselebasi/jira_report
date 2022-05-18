package com.unifin.jirareports.model.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Respuesta de consumo genérica, cuando una operación se lleva a cabo y no
 * requiere datos de respuesta
 */
@Data
public class ResultContent {
	protected String resultDescription;

	protected String resultDate;

	@JsonInclude(Include.NON_NULL)
	protected List<String> resultErrors;

	public static ResultContent buildError(String result) {
		return buildError(result, new ArrayList<>());
	}

	public static ResultContent buildError(String result, List<String> errors) {
		ResultContent content = new ResultContent(result);
		content.resultErrors = errors;
		return content;
	}

	public ResultContent(String result) {
		resultDescription = result != null ? result : "";
		
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		resultDate = sdf.format(date);
	}
}
