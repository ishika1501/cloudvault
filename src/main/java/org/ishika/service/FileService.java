package org.ishika.service;

import org.ishika.model.FileMetaData;
import org.ishika.request.FileRequest;

public interface FileService {

  FileMetaData createFile(FileRequest request);
}