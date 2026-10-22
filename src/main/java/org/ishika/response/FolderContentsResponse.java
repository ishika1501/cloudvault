package org.ishika.response;

import java.util.List;
import org.ishika.model.File;
import org.ishika.model.Folder;

public record FolderContentsResponse(
    List<Folder> folders,
    List<File> files
) {}