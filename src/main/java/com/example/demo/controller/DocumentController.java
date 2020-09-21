package com.example.demo.controller;

import com.example.demo.service.DocumentStorageService;
import com.example.demo.utils.UploadFileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/v1")
public class DocumentController {

    private final DocumentStorageService documentStorageService;

    @Autowired
    public DocumentController(DocumentStorageService documentStorageService) {
        this.documentStorageService = documentStorageService;
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("userId") Integer UserId,
                                         @RequestParam("docType") String docType) {
        String fileName = documentStorageService.storeFile(file, UserId, docType);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v1/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("userId") Integer userId,
                                                 @RequestParam("docType") String docType,
                                                 HttpServletRequest request) {
        String fileName = documentStorageService.getDocumentName(userId, docType);
        Resource resource = null;
        if (fileName != null && !fileName.isEmpty()) {
            try {
                resource = documentStorageService.loadFileAsResource(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                log.info("Could not determine file type.");
            }

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}