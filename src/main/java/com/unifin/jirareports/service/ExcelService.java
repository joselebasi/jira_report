package com.unifin.jirareports.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.unifin.jirareports.model.jira.IssueDTO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service("excelService")
public class ExcelService {

    public ByteArrayResource writeExcel(String sheetName, List<IssueDTO> lsIssue) throws IOException {

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
                "Fecha de registro", "Fecha de trabajo", "Puntos de historia" };

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
        style.setDataFormat(format.getFormat("0.00")); // custom number format
        for (IssueDTO i : lsIssue) {

            // System.out.println("rownum-before"+(rownum));
            Row row = sh.createRow(rownum++);
            // System.out.println("rownum-after"+(rownum));
            Cell cImporte = row.createCell(0);
            cImporte.setCellValue(Double.parseDouble(i.getHorasTrabajadas().toString()));
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

        Sheet sh2 = workbook.createSheet("Consolidado");
        String[] columnHeadings2 = { "Consultor", "Suma de horas trabajadas" };
        Row headerRow2 = sh2.createRow(0);
        // Iterate over the column headings to create columns
        for (int i = 0; i < columnHeadings2.length; i++) {
            Cell cell = headerRow2.createCell(i);
            cell.setCellValue(columnHeadings2[i]);
            cell.setCellStyle(headerStyle);
        }
        // Freeze Header Row
        sh2.createFreezePane(0, 1);

        List<IssueDTO> lsIssueDistinct = lsIssue.stream().filter(distinctByKey(IssueDTO::getName))
                .collect(Collectors.toList());
        int rownumsh2 = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (IssueDTO i : lsIssueDistinct) {
            // System.out.println("rownum-before"+(rownum));
            Row row = sh2.createRow(rownumsh2++);
            row.createCell(0).setCellValue(i.getRegistrador());
            // System.out.println("rownum-after"+(rownum));
            BigDecimal sum = lsIssue.stream()
                    .filter(is -> is.getName().trim().equals(i.getName().trim()))
                    .map(x -> x.getHorasTrabajadas()) // map
                    .reduce(BigDecimal.ZERO, BigDecimal::add); // reduce
            total = total.add(sum);
            Cell cImporte = row.createCell(1);
            cImporte.setCellValue(sum.doubleValue());
            cImporte.setCellStyle(style);
        }

        total.setScale(2, RoundingMode.HALF_UP);
        Row rowTotal = sh2.createRow(rownumsh2);
        Cell dTotal = rowTotal.createCell(0);
        dTotal.setCellValue("TOTAL");
        CellStyle headerStyleTotal = workbook.createCellStyle();
        headerStyleTotal.setFont(headerFont);
        headerStyleTotal.setAlignment(HorizontalAlignment.RIGHT);
        dTotal.setCellStyle(headerStyleTotal);
        Cell cTotal = rowTotal.createCell(1);
        cTotal.setCellValue(total.doubleValue());
        cTotal.setCellStyle(style);
        // Autosize columns
        for (int i = 0; i < columnHeadings2.length; i++) {
            sh2.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        System.out.println("Excel end");
        return new ByteArrayResource(outputStream.toByteArray());
        // return new ByteArrayInputStream(outputStream.toByteArray());

    }

    public void consolidado() {

    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
