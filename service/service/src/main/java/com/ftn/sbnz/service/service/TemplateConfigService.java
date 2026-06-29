package com.ftn.sbnz.service.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class TemplateConfigService {

    private static final String BASE_PATH =
            "C:\\Users\\Lenovo\\Desktop\\projects\\bank-fraud-detection\\kjar\\kjar\\src\\main\\resources\\templatetable\\";

    public List<Map<String, Object>> readTable(String fileName, String[] columns) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(BASE_PATH + fileName);
             Workbook wb = new HSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            for (int r = 2; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                Cell first = row.getCell(1); // kolona B
                if (first == null || isBlank(first)) continue;

                Map<String, Object> map = new java.util.LinkedHashMap<>();
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row.getCell(1 + i); // od kolone B
                    map.put(columns[i], readCell(cell));
                }
                rows.add(map);
            }
        }
        return rows;
    }

    public void writeTable(String fileName, String[] columns, String[] types,
                           List<Map<String, Object>> rows) throws Exception {
        Workbook wb;
        try (FileInputStream fis = new FileInputStream(BASE_PATH + fileName)) {
            wb = new HSSFWorkbook(fis);
        }
        Sheet sheet = wb.getSheetAt(0);

        int last = sheet.getLastRowNum();
        for (int r = 2; r <= last; r++) {
            Row row = sheet.getRow(r);
            if (row != null) sheet.removeRow(row);
        }

        int rIdx = 2;
        for (Map<String, Object> rowData : rows) {
            Row row = sheet.createRow(rIdx++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = row.createCell(1 + i); // od kolone B
                Object val = rowData.get(columns[i]);
                if ("num".equals(types[i])) {
                    double d = val == null ? 0 : Double.parseDouble(val.toString());
                    cell.setCellValue(d);
                } else {
                    cell.setCellValue(val == null ? "" : val.toString());
                }
            }
        }

        try (FileOutputStream fos = new FileOutputStream(BASE_PATH + fileName)) {
            wb.write(fos);
        }
        wb.close();
    }

    private boolean isBlank(Cell c) {
        if (c.getCellType() == CellType.BLANK) return true;
        if (c.getCellType() == CellType.STRING) return c.getStringCellValue().trim().isEmpty();
        return false;
    }

    private Object readCell(Cell c) {
        if (c == null) return "";
        return switch (c.getCellType()) {
            case NUMERIC -> c.getNumericCellValue();
            case STRING -> c.getStringCellValue();
            case BOOLEAN -> c.getBooleanCellValue();
            default -> "";
        };
    }
}