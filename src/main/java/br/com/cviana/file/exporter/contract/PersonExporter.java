package br.com.cviana.file.exporter.contract;

import java.util.List;

import org.springframework.core.io.Resource;

import br.com.cviana.data.dto.PersonDTO;

public interface PersonExporter {
    Resource exportPeople(List<PersonDTO> people) throws Exception;
    Resource exportPerson(PersonDTO person) throws Exception;
}
