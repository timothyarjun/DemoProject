package com.interview.demo.dto;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.interview.demo.entity.Vendor;

public class ExcelExporter {
	
	private XSSFWorkbook workBook;
	
	private XSSFSheet sheet;
	
	private List<Vendor> vendorList;

	public ExcelExporter(List<Vendor> vendorList) {
		super();
		this.vendorList = vendorList;
		workBook = new XSSFWorkbook();
		sheet = workBook.createSheet("vendor");
		
	}
	
	public void createHeader() {
		Row row = sheet.createRow(0);
		
		CellStyle style = workBook.createCellStyle();
		XSSFFont font = workBook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		
		Cell cell = row.createCell(0);
		cell.setCellValue("VendorId");
		cell.setCellStyle(style);
		
		cell = row.createCell(1);
		cell.setCellValue("Name");
		cell.setCellStyle(style);
		
		cell = row.createCell(2);
		cell.setCellValue("Phone");
		cell.setCellStyle(style);
	}
	
	public void createBody() {
		int dataRow = 1;
		
		for(Vendor vendor : vendorList) {
			Row row = sheet.createRow(dataRow++);
			
			Cell cell = row.createCell(0);
			cell.setCellValue(vendor.getId());
			sheet.autoSizeColumn(0);
						
			cell = row.createCell(1);
			cell.setCellValue(vendor.getName());
			sheet.autoSizeColumn(1);
			
			cell = row.createCell(2);
			cell.setCellValue(vendor.getPhone());
			sheet.autoSizeColumn(2);			
			
		}
		
	}
	
	public void export(HttpServletResponse res) throws IOException{
		
		createHeader();
		createBody();
		
		ServletOutputStream stream = res.getOutputStream();
		workBook.write(stream);		
		workBook.close();
		stream.close();		
	}

}
