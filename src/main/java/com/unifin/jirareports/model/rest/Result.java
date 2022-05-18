package com.unifin.jirareports.model.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Respuesta HTTP de consumo genérica, cuando una operación se lleva a cabo y no
 * requiere datos de respuesta
 */
public class Result extends ResponseEntity<ResultContent> {

    /**
     * Respuesta HTTP a partir de la información de consumo
     * @param resultDescription Descripción de la operación
     */
    public Result(String resultDescription) {
        super(new ResultContent(resultDescription), HttpStatus.OK);
    }

    Result(ResultContent error, HttpStatus status) {
        super(error, status);
    }
}
