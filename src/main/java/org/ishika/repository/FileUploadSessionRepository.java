package org.ishika.repository;

import org.ishika.model.FileUploadSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadSessionRepository extends JpaRepository<FileUploadSession, String> {

}
