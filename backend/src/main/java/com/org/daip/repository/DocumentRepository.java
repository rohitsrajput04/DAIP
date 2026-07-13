package com.db.daip.repository;

import com.db.daip.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedByIdOrderByUploadedAtDesc(Long userId);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.uploadedAt >= :startOfDay")
    long countUploadedSince(Instant startOfDay);
}
