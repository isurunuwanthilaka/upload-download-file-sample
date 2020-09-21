package com.example.demo.service;

import com.example.demo.config.DocumentStorageConfig;
import com.example.demo.exception.DocumentStorageException;
import com.example.demo.model.DocumentStorageEntity;
import com.example.demo.model.repository.DocumentStorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class DocumentStorageService {

    private final Path fileStorageLocation;

    @Autowired
    DocumentStorageRepository docStorageRepo;


    @Autowired
    public DocumentStorageService(DocumentStorageConfig documentStorageConfig) {

        this.fileStorageLocation = Paths.get(documentStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new DocumentStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }

    }

    public String storeFile(MultipartFile file, Integer userId, String docType) {

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = "";
        try {

            if (originalFileName.contains("..")) {
                throw new DocumentStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }

            String fileExtension;
            try {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } catch (Exception e) {
                fileExtension = "";
            }

            fileName = userId + "_" + docType + fileExtension;
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            DocumentStorageEntity doc = docStorageRepo.checkDocumentByUserId(userId, docType);

            if (doc != null) {
                doc.setDocumentFormat(file.getContentType());
                doc.setUploadDir("./media");
                doc.setFileName(fileName);
                docStorageRepo.save(doc);

            } else {
                DocumentStorageEntity newDoc = new DocumentStorageEntity();
                newDoc.setUserId(userId);
                newDoc.setDocumentFormat(file.getContentType());
                newDoc.setUploadDir("/media");
                newDoc.setFileName(fileName);
                newDoc.setDocumentType(docType);
                docStorageRepo.save(newDoc);
            }
            return fileName;
        } catch (IOException ex) {
            log.error(ex.getMessage());
            throw new DocumentStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }

    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }

        } catch (MalformedURLException ex) {
            log.error(ex.getMessage());
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    public String getDocumentName(Integer userId, String docType) {
        return docStorageRepo.getUploadDocumentPath(userId, docType);
    }
}