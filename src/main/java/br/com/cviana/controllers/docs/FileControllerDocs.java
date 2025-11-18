package br.com.cviana.controllers.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.data.dto.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File Endpoint")
public interface FileControllerDocs {

    @Operation(
        summary = "Download a file", 
        description = "Starts de download of a single file storage on the server",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadFileResponseDTO.class))
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
    ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request);
    
    @Operation(
        summary = "Uploads a file", 
        description = "Send and storage a new file on the server",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadFileResponseDTO.class))
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
    UploadFileResponseDTO uploadFile(MultipartFile file);
    
    @Operation(
        summary = "Uploads many files", 
        description = "Send and storage a set of files on the server",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = { @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadFileResponseDTO.class))
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
    List<UploadFileResponseDTO> uploadMultipleFile(MultipartFile[] files);

}
