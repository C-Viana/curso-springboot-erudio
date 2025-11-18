package br.com.cviana.services;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.controllers.PersonController;
import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.exceptions.BadRequestException;
import br.com.cviana.exceptions.FileStorageException;
import br.com.cviana.exceptions.RequiredObjectIsNullException;
import br.com.cviana.exceptions.ResourceNotFoundException;
import br.com.cviana.file.exporter.contract.PersonExporter;
import br.com.cviana.file.exporter.factory.FileExporterFactory;
import br.com.cviana.file.importer.contract.FileImporter;
import br.com.cviana.file.importer.factory.FileImporterFactory;
import br.com.cviana.mapper.ObjectMapper;
import br.com.cviana.models.Person;
import br.com.cviana.repository.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonService {
	
	private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());
	
	@Autowired
	PersonRepository repository;
	
	@Autowired
	FileImporterFactory importerFactory;
	
	@Autowired
	FileExporterFactory exporterFactory;

	@Autowired
	PagedResourcesAssembler<PersonDTO> assembler;

	private static void enrichWithLinks(PersonDTO dto) {
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withTitle("GET"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withTitle("GET"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).findByName("", 1, 12, "asc")).withRel("findByName").withTitle("GET"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).create(dto)).withRel("create").withTitle("POST"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class)).slash("massiveImport").withRel("massiveImport").withTitle("POST"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).update(dto)).withRel("update").withTitle("PUT"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withTitle("PATCH"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withTitle("DELETE"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PersonController.class).exportPage(1, 12, "asc", null)).withRel("exportPage").withTitle("GET"));
	}

	private PagedModel<EntityModel<PersonDTO>> parsePeople(Page<Person> people, Pageable pageable) {
		var peopleWithLinks = people.map(person -> {
			var dto = ObjectMapper.parseObject(person, PersonDTO.class);
			enrichWithLinks(dto);
			return dto;
		});
		Link findAllLink = WebMvcLinkBuilder.linkTo(
			WebMvcLinkBuilder.methodOn(PersonController.class).findAll(
				pageable.getPageNumber(), 
				pageable.getPageSize(), 
				pageable.getSort().toString()))
			.withSelfRel();

		return assembler.toModel(peopleWithLinks, findAllLink);
	}
	
	public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
		logger.info("Finding everyone");
		var people = repository.findAll(pageable);
		return parsePeople(people, pageable);
	}
	
	public Resource exportPage(Pageable pageable, String acceptHeader) {
		logger.info("Exporting list of people from database to file");
		var people = repository.findAll(pageable).map( entity -> ObjectMapper.parseObject(entity, PersonDTO.class)).getContent();
		PersonExporter exporter;
		try {
			exporter = this.exporterFactory.getExporter(acceptHeader);
			return exporter.exportPeople(people);
		} catch (Exception e) {
			throw new RuntimeException("Export file failed to complete", e);
		}
	}
	
	public PersonDTO findById(Long id) {
		logger.info("Finding one person");
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
		enrichWithLinks(dto);
		return dto;
	}
	
	public PagedModel<EntityModel<PersonDTO>> findByName(String name, Pageable pageable) {
		logger.info("Finding everyone whose name is/contains ["+name+"]");
		var people = repository.findByName(name, pageable);
		return parsePeople(people, pageable);
	}
	
	public PersonDTO create(PersonDTO person) {
		logger.info("Creating a new person");
		if(person == null) throw new RequiredObjectIsNullException();
		var entity = ObjectMapper.parseObject(person, Person.class);
		var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
		enrichWithLinks(dto);
		return dto;
	}
	
	public List<PersonDTO> massiveImport(MultipartFile file) {
		logger.info("Importing new people from file data");
		if(file.isEmpty()) throw new BadRequestException("File is empty");
		try (InputStream is = file.getInputStream()) {
			String fileName = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(() -> new BadRequestException("File name could not be determined"));
			FileImporter fileImporter = this.importerFactory.getImporter(fileName);
			List<Person> entities = fileImporter.importFile(is)
				.stream()
				.map( dto -> repository.save(ObjectMapper.parseObject(dto, Person.class)))
				.toList();
			
			return entities.stream().map( entity -> {
				var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
				enrichWithLinks(dto);
				return dto;
			}).toList();
		} catch (Exception e) {
			logger.error("File import failed and could not be processed");
			throw new FileStorageException("File import failed and could not be processed", e);
		}
	}
	
	public PersonDTO update(PersonDTO person) {
		logger.info("Updating one person");
		if(person == null) throw new RequiredObjectIsNullException();
		var entity = ObjectMapper.parseObject(findById(person.getId()), Person.class);
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		entity.setEnabled(person.isEnabled());
		entity.setProfileUrl(person.getProfileUrl());
		entity.setPhotoUrl(person.getPhotoUrl());
		var dto = ObjectMapper.parseObject(repository.save(entity), PersonDTO.class);
		enrichWithLinks(dto);
		return dto;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one person");
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}
	
	@Transactional
	public PersonDTO disablePerson(Long id) {
		logger.info("Setting a person as disabled");
		repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.disablePerson(id);
		//The repository must be configured with attribute 'clearAutomatically' so this new search won't go to the cache
		var entity = repository.findById(id).get();
		var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
		enrichWithLinks(dto);
		return dto;
	}
	
	public Resource exportPerson(Long id, String acceptHeader) {
		logger.info("Exporting data of a single person");
		PersonExporter exporter;
		try {
			var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
			var dto = ObjectMapper.parseObject(entity, PersonDTO.class);
			enrichWithLinks(dto);
			exporter = this.exporterFactory.getExporter(acceptHeader);
			return exporter.exportPerson(dto);
		} catch (Exception e) {
			throw new RuntimeException("Export file of person data failed to complete", e);
		}
	}

}
