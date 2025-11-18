package br.com.cviana.controllers.docs;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.cviana.data.dto.BookDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface BookControllerDocs {

    @Operation(
        summary = "Finds all Books", 
        description = "Finds all Books",
        tags = {"Books"},
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                array = @ArraySchema(schema = @Schema(implementation = BookDTO.class)))
		    }),
            @ApiResponse(
                description = "No Content", 
                responseCode = "204",
                content = @Content),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content),
        }
        )
    ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
        @RequestParam(value = "page") Integer page,
        @RequestParam(value = "quantity") Integer quantity,
        @RequestParam(value = "sort") String sort
    );
    
    @Operation(
        summary = "Finds a Book", 
        description = "Finds a single register of Book",
        tags = {"Books"},
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookDTO.class))
		    }),
            @ApiResponse(
                description = "No Content", 
                responseCode = "204",
                content = @Content),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content),
        }
        )
    BookDTO findById(Long id);
    
    @Operation(
        summary = "Creates a Book", 
        description = "Persists a new register of Book into the database",
        tags = {"Books"},
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookDTO.class))
		    }),
            @ApiResponse(
                description = "No Content", 
                responseCode = "204",
                content = @Content),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content),
        }
        )
    BookDTO create(BookDTO book);

    @Operation(
        summary = "Updates a Book", 
        description = "Updates the register of a Book on the database",
        tags = {"Books"},
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = BookDTO.class))
		    }),
            @ApiResponse(
                description = "No Content", 
                responseCode = "204",
                content = @Content),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content),
        }
        )
    BookDTO update(BookDTO book);

    @Operation(
        summary = "Deletes a single Book", 
        description = "Removes the register of a Book from the database",
        tags = {"Books"},
        responses = {
            @ApiResponse(
                description = "Success", 
                responseCode = "204",
                content = @Content),
            @ApiResponse(
                description = "Bad Request", 
                responseCode = "400",
                content = @Content),
            @ApiResponse(
                description = "Unauthorized", 
                responseCode = "401",
                content = @Content),
            @ApiResponse(
                description = "Not Found", 
                responseCode = "404",
                content = @Content),
            @ApiResponse(
                description = "Internal Error", 
                responseCode = "500",
                content = @Content),
        }
        )
    ResponseEntity<?> delete(Long id);

}