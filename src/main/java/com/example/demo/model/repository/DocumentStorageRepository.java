package com.example.demo.model.repository;

import com.example.demo.model.DocumentStorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentStorageRepository extends JpaRepository<DocumentStorageEntity, Integer> {

    @Query("Select a from DocumentStorageEntity a where user_id = ?1 and document_type = ?2")
    DocumentStorageEntity checkDocumentByUserId(Integer userId, String docType);

    @Query("Select fileName from DocumentStorageEntity a where user_id = ?1 and document_type = ?2")
    String getUploadDocumentPath(Integer userId, String docType);
}