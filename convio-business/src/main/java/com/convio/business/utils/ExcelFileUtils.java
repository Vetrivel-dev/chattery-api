package com.convio.business.utils;

import java.io.IOException;
import java.util.List;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.convio.business.dto.Business;

public class ExcelFileUtils {

	public  byte[]  generateBusinessTemplate(List<Business> business) 
	{
        try(Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("Business List");
            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Creating header
            Cell cell = row.createCell(0);
            cell.setCellValue("Id");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("ClientId");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("WebsiteUrl");
            cell.setCellStyle(headerCellStyle);  
            
            cell = row.createCell(3);
            cell.setCellValue("Name");     
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Address");     
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Phone");     
            cell.setCellStyle(headerCellStyle);

            for(int i = 0; i < business.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(business.get(i).getId());
                dataRow.createCell(1).setCellValue(business.get(i).getIdClient());
                dataRow.createCell(2).setCellValue(business.get(i).getWebsiteUrl());
                dataRow.createCell(3).setCellValue(business.get(i).getName());
                dataRow.createCell(4).setCellValue(business.get(i).getAddress());
                dataRow.createCell(5).setCellValue(business.get(i).getPhone());
            }
            // Making size of column auto resize to fit with data
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


