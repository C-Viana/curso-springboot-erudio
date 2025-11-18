package br.com.cviana.controllers.docs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import br.com.cviana.data.dto.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface AuthControllerDocs {

    @Operation(summary = "Authenticate user", description = "Signin user with password and creates an access token", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {
			@Content( mediaType = MediaType.APPLICATION_JSON_VALUE ),
			@Content( mediaType = MediaType.APPLICATION_XML_VALUE ),
			@Content( mediaType = MediaType.APPLICATION_YAML_VALUE )
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<?> signin(AccountCredentialsDTO credentials);

    @Operation(summary = "Review user access", description = "Refreshes an user's access through a valid refresh token", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200"),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<?> refresh(String username, String refreshToken);

    @Operation(summary = "Save new user", description = "Create and save a new user into the database", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {
			@Content( mediaType = MediaType.APPLICATION_JSON_VALUE ),
			@Content( mediaType = MediaType.APPLICATION_XML_VALUE ),
			@Content( mediaType = MediaType.APPLICATION_YAML_VALUE )
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<?> create(AccountCredentialsDTO credentials);

}