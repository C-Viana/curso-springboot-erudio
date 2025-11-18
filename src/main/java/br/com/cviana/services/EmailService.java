package br.com.cviana.services;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.cviana.config.EmailConfig;
import br.com.cviana.data.dto.request.EmailRequestDto;
import br.com.cviana.mail.EmailSender;

@Service
public class EmailService {
    
    @Autowired
    private EmailSender sender;

    @Autowired
    private EmailConfig config;

    public void sendSimpleEmail(EmailRequestDto request) {
        System.out.println("USER: "+config.getUsername());
        System.out.println("PASS: "+config.getPassword());
        sender.to(request.getTo()).withSubject(request.getSubject()).withMessage(request.getBody())
            .send(config);
    }

    public void sendEmailWithAttachment(String requestJson, MultipartFile multipartFile) {
        EmailRequestDto request = null;
        File tempFile = null;

        try {
            request = new ObjectMapper().readValue(requestJson, EmailRequestDto.class);
            tempFile = File.createTempFile("attachment", multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
            sender.to(request.getTo()).withSubject(request.getSubject()).withMessage(request.getBody()).attach(tempFile.getAbsolutePath())
                .send(config);
        } catch (JsonMappingException e) {
            throw new RuntimeException("Error parsing e-mail request JSON", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing e-mail requet JSON content", e);
        } catch (IllegalStateException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Error processing attached files", e);
        } finally {
            if(tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

}
