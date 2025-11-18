package br.com.cviana.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.cviana.config.FileStorageConfig;
import br.com.cviana.exceptions.FileNotFoundException;
import br.com.cviana.exceptions.FileStorageException;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    public FileStorageService(FileStorageConfig fileConfig) {
        
        this.fileStorageLocation = Paths.get(fileConfig.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            logger.error("Storage directory could not be created!", e);
            throw new FileStorageException("Storage directory could not be created!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                logger.error("File name contains invalid path sequence: "+fileName);
                throw new FileStorageException("File name contains invalid path sequence: "+fileName);
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File uploaded successfully");
            return fileName;
        } catch (Exception e) {
            logger.error("ERROR to save file ["+fileName+"]", e);
            throw new FileStorageException("ERROR to save file ["+fileName+"]", e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource res = new UrlResource(filePath.toUri());
            if(res.exists()) return res;
            else throw new FileNotFoundException("File not found on server: "+fileName);
        } catch (Exception e) {
            logger.error("File not found on server: "+fileName, e);
            throw new FileNotFoundException("File not found on server: "+fileName, e);
        }
    }
}
