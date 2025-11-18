package br.com.cviana.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.controllers.docs.EmailControllerDocs;
import br.com.cviana.data.dto.request.EmailRequestDto;
import br.com.cviana.services.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/email/v1")
@Tag(name = "E-mails", description = "Endpoints for Managing E-mails")
public class EmailController implements EmailControllerDocs {

    @Autowired
    private EmailService service;

    @Override
    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDto emailReq) {
        service.sendSimpleEmail(emailReq);
        return new ResponseEntity<>("E-mail sent successfully", HttpStatus.OK);
    }

    @Override
    @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmailWithAttached(@RequestParam("emailRequest") String emailRequest, @RequestParam("attachment") MultipartFile attachment) {
        service.sendEmailWithAttachment(emailRequest, attachment);
        return new ResponseEntity<>("E-mail with attachment(s) sent successfully", HttpStatus.OK);
    }

}
