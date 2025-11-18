package br.com.cviana.file.importer.implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.importer.contract.FileImporter;

@Component
public class CsvImporter implements FileImporter {

    @Override
    public List<PersonDTO> importFile(InputStream inputStream) throws Exception {
        CSVFormat format = CSVFormat.Builder.create()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .build();
        
        Iterable<CSVRecord> records = format.parse(new InputStreamReader(inputStream));

        return parseRecordsToPersonDto(records);
    }

    private List<PersonDTO> parseRecordsToPersonDto(Iterable<CSVRecord> records) {
        List<PersonDTO> people = new ArrayList<>();
        for (CSVRecord rec : records) {
            PersonDTO newPerson = new PersonDTO();
            newPerson.setFirstName(rec.get("first_name"));
            newPerson.setLastName(rec.get("last_name"));
            newPerson.setGender(rec.get("gender"));
            newPerson.setAddress(rec.get("address"));
            newPerson.setEnabled(true);
            people.add( newPerson );
        }
        return people;
    }

}
