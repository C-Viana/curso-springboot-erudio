package br.com.cviana.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.cviana.controllers.docs.FileControllerDocs;
import br.com.cviana.data.dto.UploadFileResponseDTO;
import br.com.cviana.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/file/v1")
public class FileController implements FileControllerDocs {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService service;

    @Override
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        logger.info("Starting file download");
        //http://localhost:8080/api/file/v1/downloadFile/TextoParaCorrecao-2.txt
        //http://localhost:8080/api/file/v1/downloadFile/Atividade%20Policial.jpg
        //http://localhost:8080/api/file/v1/downloadFile/Battle%20Royale%20-%202000.jpg
        //http://localhost:8080/api/file/v1/downloadFile/Frankenstein%20(2025).jpg
        Resource res = service.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(res.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.error("Could not determine file extension", e);
        }
        if(contentType == null) contentType = "application/octet-stream";
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+res.getFilename()+"\"")
            .body(res);
    }

    @Override
    @PostMapping("/upload")
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("Starting file upload");
        var fileName = service.storeFile(file);
        var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/v1/downloadFile/").path(fileName).toUriString();
        return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @Override
    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponseDTO> uploadMultipleFile(@RequestParam("files") MultipartFile[] files) {
        logger.info("Starting download of ["+files.length+"] files");
        return Arrays.asList(files).stream().map( file -> uploadFile(file)).collect(Collectors.toList());
    }

}
