package com.example.demo.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;

@Entity
@Table(name = "merchant_documents")
@Data
public class DocumentStorageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "user_id")
    private Integer UserId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_format")
    private String documentFormat;

    @Column(name = "upload_dir")
    private String uploadDir;
}