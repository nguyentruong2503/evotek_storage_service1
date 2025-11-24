package com.example.storage.service;

import com.example.common.model.dto.FileDTO;
import com.example.common.model.request.FileSearchRequest;
import com.example.common.model.request.UpdateFileRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface FileService {
    Page<FileDTO> getAll(FileSearchRequest fileSearchRequest, int page, int size);

    List<FileDTO> uploadMultipleFiles(MultipartFile[] files, Boolean isPublic,Long ownerID) throws Exception;

    FileDTO getFileById(Long id);

    FileDTO getPublicFile(Long id) throws AccessDeniedException;

    //hiển thị view với file ảnh
    String getPrivateViewUrl(Long id, Integer width, Integer height, Double ratio,Long ownerID) throws AccessDeniedException;

    String getPublicViewUrl(Long id, Integer width, Integer height, Double ratio) throws AccessDeniedException;

    void deleteFile(Long id);

    FileDTO updateFile(Long id, UpdateFileRequest request);

    byte[] downloadFileContent(Long id);

}
