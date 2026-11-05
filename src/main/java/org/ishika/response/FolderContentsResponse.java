package org.ishika.response;

import java.util.List;
import org.ishika.model.FileMetaData;
import org.ishika.model.Folder;

public record FolderContentsResponse(
    List<Folder> folders,
    List<FileMetaData> files
) {}