package com.jt.sys.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * excel导入及导出工具
 * 
 * @author Administrator
 *
 */
public class ExportDBUtil {
	
	private static HSSFWorkbook workbook;
	static {
		workbook = new HSSFWorkbook();
	}
	
	public static void exportExcel(
			String headerName, 
			List<String> tableHeaders, 
			List<?> data,
			HttpServletResponse response) throws IOException {
		HSSFSheet sheet = workbook.createSheet();
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Font font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);

		// 将第一行的11个单元格给合并
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
		HSSFRow row = sheet.createRow(0);
		HSSFCell beginCell = row.createCell(0);
		beginCell.setCellValue(headerName);
		beginCell.setCellStyle(cellStyle);

		row = sheet.createRow(1);
		// 创建表头
		for (int i = 0; i < tableHeaders.size(); i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(tableHeaders.get(i));
			cell.setCellStyle(cellStyle);
		}

		List<Map<?, ?>> tmpData = getMapperResult(data);
		for (int i = 0; i < tmpData.size(); i++) {
			row = sheet.createRow(i + 2);
			Map<?, ?> map = tmpData.get(i);
			
			for (int j = 0; j < tableHeaders.size(); j++) {
				String key = tableHeaders.get(j);
				Object value = map.get(key);
				if (value != null) {	
					HSSFCell cell = row.createCell(j);
					if (value instanceof Date) {
						// 日期格式化
						HSSFCellStyle dateCellStyle = workbook.createCellStyle();
						HSSFCreationHelper creationHelper = workbook.getCreationHelper();
						dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
						cell.setCellStyle(dateCellStyle);
						cell.setCellValue((Date)value);
//						sheet.setColumnWidth(i, 20 * 256);
					} else {		
						String valStr = value.toString();
						cell.setCellValue(valStr);
//						sheet.setColumnWidth(j, (valStr.length() + 10) * 256);
					}
					sheet.autoSizeColumn(j);
				}
			}
		}
		
		ServletOutputStream out = response.getOutputStream();
		response.reset();
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment;filename=users.xls");
		workbook.write(out);
		out.flush();
		workbook.close();
		out.close();
	}

	public static void exportPDF(
			String headerName, 
			List<String> tableHeaders, 
			List<?> data,
			HttpServletResponse response) throws Exception {
		ServletOutputStream out = response.getOutputStream();
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, out);
		writer.setViewerPreferences(PdfWriter.PageModeUseThumbs);
		document.setPageSize(PageSize.A2);//设置A2
		document.open(); 
		
		PdfPTable table = new PdfPTable(tableHeaders.size());
		table.setHorizontalAlignment(Element.ALIGN_CENTER);
		
		//设置中文字体和字体样式  
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);    
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.NORMAL);

		// 表标题
		PdfPCell titleCell = getPdfCell(headerName, Element.ALIGN_CENTER, font); 
		titleCell.setColspan(tableHeaders.size());
		table.addCell(titleCell);
		
		// 表头
		for (int i = 0; i < tableHeaders.size(); i++) {
			String name = tableHeaders.get(i);
	        table.addCell(getPdfCell(name, Element.ALIGN_CENTER, font));
		}
		
		List<Map<?, ?>> tmpData = getMapperResult(data);
		for (int i = 0; i < tmpData.size(); i++) {
			Map<?, ?> map = tmpData.get(i);
			for (int j = 0; j < tableHeaders.size(); j++) {
				String key = tableHeaders.get(j);
				Object value = map.get(key);
				String valStr = "";
				if (value != null) {
					if (value instanceof Date) {
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						valStr = format.format(value);
					} else {
						valStr = value.toString();
					}
				}
		        table.addCell(getPdfCell(valStr, Element.ALIGN_LEFT, font));
			}
		}
		
		response.reset();
		response.setContentType("application/pdf");
		response.setHeader("Content-disposition", "attachment;filename=users.pdf");
		document.add(table); //pdf文档中加入table
		document.close();
	}
	
	private static PdfPCell getPdfCell(String val, int horizontalAlignment, com.itextpdf.text.Font font) {
		PdfPCell pdfCell = new PdfPCell(); //表格的单元格
		pdfCell.setHorizontalAlignment(horizontalAlignment);
		pdfCell.setVerticalAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph = new Paragraph(val, font);
        pdfCell.setPhrase(paragraph);
        return pdfCell;
	}
	
	private static List<Map<?, ?>> getMapperResult(List<?> data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<?, ?>> tmpData = new ArrayList<>();
		for (Object obj : data) {
			String json = mapper.writeValueAsString(obj);
			Map<?, ?> map = mapper.readValue(json, Map.class);
			tmpData.add(map);
		}
		return tmpData;
	} 
	
}













