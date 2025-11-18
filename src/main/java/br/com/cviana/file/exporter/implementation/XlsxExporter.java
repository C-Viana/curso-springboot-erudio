package br.com.cviana.file.exporter.implementation;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.exporter.contract.PersonExporter;

@Component
public class XlsxExporter implements PersonExporter {

    @Override
    public Resource exportPeople(List<PersonDTO> people) throws Exception {
        String[] headerColumns = {"ID", "First name", "Last name", "Gender", "Address", "Enabled"};

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("PEOPLE");
            Row header = sheet.createRow(0);
            
            for (int i = 0; i < headerColumns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headerColumns[i]);
                cell.setCellStyle(createHeaderCellStyle(workbook));
            }
            
            int index = 1;
            for (PersonDTO dto : people) {
                Row row = sheet.createRow(index);
                row.createCell(0).setCellValue(dto.getId());
                row.createCell(1).setCellValue(dto.getFirstName());
                row.createCell(2).setCellValue(dto.getLastName());
                row.createCell(3).setCellValue(dto.getGender());
                row.createCell(4).setCellValue(dto.getAddress());
                String enabled = (dto.isEnabled() == true) ? "yes" : "no";
                row.createCell(5).setCellValue(enabled);
                index++;
            }

            for (int i = 0; i < headerColumns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            return new ByteArrayResource(output.toByteArray());
        }
    }

    private CellStyle createHeaderCellStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    @Override
    public Resource exportPerson(PersonDTO person) throws Exception {
        throw new UnsupportedOperationException("NOT SUPPORTED FOR XLSX FORMAT. USE PDF EXPORTATION");
    }

}
