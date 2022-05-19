package com.unifin.jirareports.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.unifin.jirareports.model.jira.IssueDTO;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@Service("csvFileService")
public class CSVFileService {

    public StringWriter writeCSVFile(List<IssueDTO> issueDTO) {
        ICsvBeanWriter beanWriter = null;
        StringWriter sw = null;
        try {
            sw = new StringWriter();
            beanWriter = new CsvBeanWriter(sw, CsvPreference.STANDARD_PREFERENCE);
            String[] header = {"horasTrabajadas", "key", "proyecto", "asignacion", "registrador", "fecha de registro", "fecha de trabajo", "puntos de historia"};
            beanWriter.writeHeader(header);
            for (IssueDTO issue : issueDTO) {
                beanWriter.write(issue, header);
            }
        } catch (IOException e) {
            System.err.println("Error writing the CSV file: " + e);
        } finally {
            if (beanWriter != null) {
                try {
                    beanWriter.close();
                } catch (IOException e) {
                    System.err.println("Error closing the writer: " + e);
                }
            }
        }

        return sw;
    }    
}
