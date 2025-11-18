package br.com.cviana.file.importer.implementation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.importer.contract.FileImporter;

@Component
public class XlsxImporter implements FileImporter {

    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if(rows.hasNext()) rows.next();
            return parseRowsToPersonDto(rows);
        }
    }

    private List<PersonDTO> parseRowsToPersonDto(Iterator<Row> rows) {
        List<PersonDTO> people = new ArrayList<>();
        while (rows.hasNext()) {
            Row row = rows.next();
            PersonDTO newPerson = new PersonDTO();
            if(isRowValid(row)) {
                newPerson.setFirstName(row.getCell(0).getStringCellValue());
                newPerson.setLastName(row.getCell(1).getStringCellValue());
                newPerson.setGender(row.getCell(2).getStringCellValue());
                newPerson.setAddress(row.getCell(3).getStringCellValue());
                newPerson.setEnabled(true);
                people.add( newPerson );
            }
        }
        return people;
    }

    private boolean isRowValid(Row row) {
        return row.getCell(0)!=null && row.getCell(0).getCellType()!=CellType.BLANK;
    }

}
