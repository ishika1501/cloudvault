package org.ishika.service;

import org.ishika.model.FileMetaData;
import org.ishika.model.FileUploadSession;
import org.ishika.request.FileUploadSessionRequest;

public interface FileUploadSessionService {

  FileUploadSession initiateFileUploadSession(FileUploadSessionRequest request);
}
