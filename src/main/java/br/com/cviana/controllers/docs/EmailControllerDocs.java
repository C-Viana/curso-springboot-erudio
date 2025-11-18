package br.com.cviana.controllers.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.data.dto.request.EmailRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface EmailControllerDocs {
    
    @Operation(
        summary = "Sends an e-mail", 
        description = "Sends an e-mail message with a body of text content to one or more contacts",
        tags = {"E-mails"},
        responses = {
            @ApiResponse(description = "Success",responseCode = "200",content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500",content = @Content),
        }
        )
    ResponseEntity<String> sendEmail(EmailRequestDto emailReqDto);
    
    @Operation(
        summary = "Sends an e-mail with attached file(s)", 
        description = "Sends an e-mail message with a body and of text content and files to one or more contacts",
        tags = {"E-mails"},
        responses = {
            @ApiResponse(description = "Success",responseCode = "200",content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400",content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401",content = @Content),
            @ApiResponse(description = "Internal Error", responseCode = "500",content = @Content),
        }
        )
    ResponseEntity<String> sendEmailWithAttached(String requestJson, MultipartFile multipartFile);

}