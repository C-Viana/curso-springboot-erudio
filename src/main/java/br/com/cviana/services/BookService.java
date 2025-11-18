package br.com.cviana.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import br.com.cviana.controllers.BookController;
import br.com.cviana.data.dto.BookDTO;
import br.com.cviana.exceptions.RequiredObjectIsNullException;
import br.com.cviana.exceptions.ResourceNotFoundException;
import br.com.cviana.mapper.ObjectMapper;
import br.com.cviana.models.Book;
import br.com.cviana.repository.BookRepository;

@Service
public class BookService {

    private Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;

    private static void enrichWithLinks(BookDTO dto) {
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).findById(dto.getId())).withSelfRel().withTitle("GET"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).findAll(0, 10, "asc")).withRel("findAll").withTitle("GET"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).create(dto)).withRel("create").withTitle("POST"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).update(dto)).withRel("update").withTitle("PUT"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).delete(dto.getId())).withRel("delete").withTitle("DELETE"));
	}

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
		logger.info("SERVICE_LOG::RUNNING Requirement of all books");
        Page<Book> library = repository.findAll(pageable);
        
        var booksWithLinks = library.map( book -> {
            BookDTO dto = ObjectMapper.parseObject(book, BookDTO.class);
            enrichWithLinks(dto);
            return dto;
        });

        Link link = WebMvcLinkBuilder.linkTo(
            WebMvcLinkBuilder.methodOn(BookController.class)
            .findAll(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                pageable.getSort().toString()))
            .withSelfRel();
        
        return assembler.toModel(booksWithLinks, link);
    }

    public BookDTO findById(Long id) {
		logger.info("SERVICE_LOG::RUNNING Requirement of book by id {}", id);
        BookDTO dto = ObjectMapper.parseObject(
            repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SERVICE_LOG::FAILED Book id {"+id+"} not found")), 
            BookDTO.class);
        enrichWithLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO book) {
        logger.info("SERVICE_LOG::RUNNING Creation of a new book record");
        if(book == null) throw new RequiredObjectIsNullException("SERVICE_LOG::FAILED Cannot create a null book record");
        Book savedEntity = repository.save(ObjectMapper.parseObject(book, Book.class));
        BookDTO dto = ObjectMapper.parseObject(savedEntity, BookDTO.class);
        enrichWithLinks(dto);
        return dto;
    }

    public BookDTO update(BookDTO bookDTO) {
        if(bookDTO == null)
            throw new RequiredObjectIsNullException("SERVICE_LOG::FAILED Cannot create a null book record");
		logger.info("SERVICE_LOG::RUNNING Update of book record with id {}", bookDTO.getId());
        
        var updatedEntity = ObjectMapper.parseObject(findById(bookDTO.getId()), Book.class);
        updatedEntity.setAuthor(bookDTO.getAuthor());
        updatedEntity.setTitle(bookDTO.getTitle());
        updatedEntity.setPublishedDate(bookDTO.getPublishedDate());
        updatedEntity.setPrice(bookDTO.getPrice());

        var dto = ObjectMapper.parseObject(repository.save(updatedEntity), BookDTO.class);
        enrichWithLinks(dto);
        return dto;
    }

    public void delete(Long id) {
		logger.info("SERVICE_LOG::RUNNING Deletion of book record with id {}", id);
        Book book = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SERVICE_LOG::FAILED Book id {"+id+"} not found"));
        repository.delete(book);
    }

}
