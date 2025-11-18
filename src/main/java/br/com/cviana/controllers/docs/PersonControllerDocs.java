package br.com.cviana.controllers.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.data.dto.PersonDTO;
import br.com.cviana.file.exporter.MediaTypes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PersonControllerDocs {

    @Operation(summary = "Finds all People", description = "Return list of all registered people", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class)))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
		@RequestParam(value = "page", defaultValue = "0") Integer page, 
		@RequestParam(value = "size", defaultValue = "10") Integer size,
		@RequestParam(value = "direction", defaultValue = "asc") String direction
	);

    @Operation(summary = "Exports a list of people", description = "Creates a XLSX or CSV file with all people according the given search parameters", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {
			@Content( mediaType = MediaTypes.APPLICATION_XLSX_VALUE ),
			@Content( mediaType = MediaTypes.APPLICATION_CSV_VALUE ),
			@Content( mediaType = MediaTypes.APPLICATION_PDF_VALUE )
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<Resource> exportPage(
		@RequestParam(value = "page", defaultValue = "0") Integer page, 
		@RequestParam(value = "size", defaultValue = "10") Integer size,
		@RequestParam(value = "direction", defaultValue = "asc") String direction,
		HttpServletRequest request
	);

    @Operation(summary = "Finds a Person by ID", description = "Return a specific person by their identifier", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    PersonDTO findById(Long id);

    @Operation(summary = "Finds a Person by name", description = "Return a specific person by their first or last name", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
		@PathVariable(value = "name") String name,
		@RequestParam(value = "page", defaultValue = "0") Integer page, 
		@RequestParam(value = "size", defaultValue = "10") Integer size,
		@RequestParam(value = "direction", defaultValue = "asc") String direction
	);

    @Operation(summary = "Register a Person", description = "Persists a new person in the database", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    PersonDTO create(PersonDTO person);

    @Operation(summary = "Massive person creation", description = "Import file XLSX or CSV for multiple person creation", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {
			@Content(schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    List<PersonDTO> massiveImport( MultipartFile file );

    @Operation(summary = "Update a Person's data", description = "Updates a person's information in the database", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    PersonDTO update(PersonDTO person);

    @Operation(summary = "Delete a single Person", description = "Deletes a person from the database", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<?> delete(Long id);

	@Operation(summary = "Disables a Person", description = "Set a person's status to disabled", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
			mediaType = MediaType.APPLICATION_JSON_VALUE, 
			schema = @Schema(implementation = PersonDTO.class))
		}),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    PersonDTO disablePerson(Long id);

    @Operation(summary = "Exports data of a single Person to file", description = "Creates a PDF file with data of a single person according the given ID", 
		tags = {"People"}, responses = {
		@ApiResponse(description = "Success", responseCode = "200", content = @Content( mediaType = MediaTypes.APPLICATION_PDF_VALUE )),
		@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
		@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
		@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
		@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
		@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
	})
    ResponseEntity<Resource> exportPerson(@PathVariable("id") Long id);

}