package com.unifin.jirareports.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.unifin.jirareports.model.jira.IssueDTO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service("excelService")
public class ExcelService {

    public ByteArrayResource writeExcel(String sheetName, ArrayList<IssueDTO> lsIssue) throws IOException {

        // Create workbook in .xlsx format
        XSSFWorkbook workbook = new XSSFWorkbook();
        // For .xsl workbooks use new HSSFWorkbook();
        // Create Sheet
        Sheet sh = workbook.createSheet(sheetName);
        // Create top row with column headings
        // String[] columnHeadings = {"Fecha", "Emisor", "Regimen Fiscal",
        // "RFC","Descripcion", "Importe", "IVA","Forma Pago","Metodo Pago", "Subtotal",
        // "Total", "XML"};
        String[] columnHeadings = { "Horas trabajadas", "Key", "Proyecto", "Asignacion", "Registrador",
                "Fecha de registro", "Fecha de trabajo", "Pustos de historia" };

        // We want to make it bold with a foreground color.
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.index);
        // Create a CellStyle with the font
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        // Create the header row
        Row headerRow = sh.createRow(0);
        // Iterate over the column headings to create columns
        for (int i = 0; i < columnHeadings.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeadings[i]);
            cell.setCellStyle(headerStyle);
        }
        // Freeze Header Row
        sh.createFreezePane(0, 1);
        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("MM/dd/yyyy"));
        int rownum = 1;

        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("0.0")); // custom number format
        for (IssueDTO i : lsIssue) {

            // System.out.println("rownum-before"+(rownum));
            Row row = sh.createRow(rownum++);
            // System.out.println("rownum-after"+(rownum));
            Cell cImporte = row.createCell(0);
            cImporte.setCellValue(Double.parseDouble(i.getHorasTrabajadas()));
            cImporte.setCellStyle(style);
            row.createCell(1).setCellValue(i.getKey());
            row.createCell(2).setCellValue(i.getProyecto());
            row.createCell(3).setCellValue(i.getAsignacion());
            row.createCell(4).setCellValue(i.getRegistrador());
            row.createCell(5).setCellValue(i.getFecharegistro());
            row.createCell(6).setCellValue(i.getFechatrabajo());
            row.createCell(7).setCellValue(i.getPuntoshistoria());

        }
        // Autosize columns
        for (int i = 0; i < columnHeadings.length; i++) {
            sh.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        System.out.println("Excel end");
        return new ByteArrayResource(outputStream.toByteArray());
        // return new ByteArrayInputStream(outputStream.toByteArray());

    }

}
