package br.com.cviana.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cviana.controllers.docs.BookControllerDocs;
import br.com.cviana.data.dto.BookDTO;
import br.com.cviana.services.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Books", description = "Endpoints for Managing Books")
public class BookController implements BookControllerDocs {

    @Autowired
    private BookService service;

    @Override
    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE, 
        MediaType.APPLICATION_XML_VALUE, 
        MediaType.APPLICATION_YAML_VALUE}
        )
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "1") Integer page, 
            @RequestParam(value = "quantity", defaultValue = "10") Integer quantity, 
            @RequestParam(value = "sort", defaultValue = "asc") String sort) {
        var sortSet = "desc".equalsIgnoreCase(sort) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, quantity, Sort.by(sortSet, "title"));
        return ResponseEntity.ok( service.findAll(pageable) );
    }

    @Override
    @GetMapping(value = "/{id}", produces = {
        MediaType.APPLICATION_JSON_VALUE, 
        MediaType.APPLICATION_XML_VALUE, 
        MediaType.APPLICATION_YAML_VALUE}
        )
    public BookDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Override
    @PostMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}, 
        consumes = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}
        )
    public BookDTO create(@RequestBody BookDTO book) {
        return service.create(book);
    }

    @Override
    @PutMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}, 
        consumes = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}
        )
    public BookDTO update(@RequestBody BookDTO book) {
        return service.update(book);
    }
    
    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
