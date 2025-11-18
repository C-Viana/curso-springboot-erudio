package br.com.cviana.controllers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cviana.controllers.docs.AuthControllerDocs;
import br.com.cviana.data.dto.security.AccountCredentialsDTO;
import br.com.cviana.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentitacion endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDocs {
    static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthService service;

    @Override
    @PostMapping(
        value = "/signin",
        produces = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}, 
        consumes = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {
        if(isCredentialsValid(credentials))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        
        var token = service.signin(credentials);
        if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        return token;
    }

    @Override
    @PutMapping(
        value = "/refresh/{username}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<?> refresh(@PathVariable String username, @RequestHeader("Authorization") String refreshToken) {
        if(!isParametersValid(username, refreshToken))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        
        var token = service.refresh(username, refreshToken.split(" ")[1]);
        if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");

        return token;
    }

    @Override
    @PostMapping(
        value = "/createUser", //createUser ENDPOINT SHOULD NOT BE EXPOSED ON PRODUCTION. USE IT ONLY FOR TESTS
        produces = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE}, 
        consumes = {
            MediaType.APPLICATION_JSON_VALUE, 
            MediaType.APPLICATION_XML_VALUE, 
            MediaType.APPLICATION_YAML_VALUE})
    public ResponseEntity<?> create(@RequestBody AccountCredentialsDTO credentials) {
        service.create(credentials);
        return ResponseEntity.ok(credentials);
    }

    private boolean isParametersValid(String username, String refreshToken) {
        boolean usernameTest = !(username == null || username.isBlank());
        boolean tokenTest = !(refreshToken == null || refreshToken.isBlank() || (!refreshToken.startsWith("Bearer")));
        return usernameTest && tokenTest;
    }

    private boolean isCredentialsValid(AccountCredentialsDTO credentials) {
        return credentials == null || StringUtils.isBlank(credentials.getUsername()) || StringUtils.isBlank(credentials.getPassword());
    }
}
