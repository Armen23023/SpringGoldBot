package am.relex.service;

import am.relex.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {

    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
