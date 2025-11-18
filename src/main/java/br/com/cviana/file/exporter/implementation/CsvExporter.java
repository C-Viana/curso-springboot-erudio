package br.com.cviana.file.exporter.implementation;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.exporter.contract.PersonExporter;

@Component
public class CsvExporter implements PersonExporter {

    @Override
    public Resource exportPeople(List<PersonDTO> people) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
        
        CSVFormat format = CSVFormat.Builder.create()
            .setHeader("ID", "First name", "Last name", "Gender", "Address", "Enabled")
            .setSkipHeaderRecord(false)
            .setTrim(true)
            .build();
        
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (PersonDTO dto : people) {
                printer.printRecord(
                    dto.getId(), dto.getFirstName(), dto.getLastName(), dto.getGender(), dto.getAddress(), dto.isEnabled()
                );
            }
        }

        return new ByteArrayResource(output.toByteArray());
    }

    @Override
    public Resource exportPerson(PersonDTO person) throws Exception {
        throw new UnsupportedOperationException("NOT SUPPORTED FOR CSV FORMAT. USE PDF EXPORTATION");
    }

}
