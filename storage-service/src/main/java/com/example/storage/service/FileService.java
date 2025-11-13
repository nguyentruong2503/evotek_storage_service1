package com.example.storage.service;

import com.example.storage.model.dto.FileDTO;
import com.example.storage.model.request.FileSearchRequest;
import com.example.storage.model.request.UpdateFileRequest;
import com.example.storage.model.response.PagedResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

public interface FileService {
    PagedResponse<FileDTO> getAll(FileSearchRequest fileSearchRequest, int page, int size);

    List<FileDTO> uploadMultipleFiles(MultipartFile[] files, Boolean isPublic,Long ownerID);

    FileDTO getFileById(Long id);

    FileDTO getPublicFile(Long id) throws AccessDeniedException;

    //hiển thị view với file ảnh
    String getPrivateViewUrl(Long id, Integer width, Integer height, Double ratio,Long ownerID) throws AccessDeniedException;

    String getPublicViewUrl(Long id, Integer width, Integer height, Double ratio) throws AccessDeniedException;

    void deleteFile(Long id);

    FileDTO updateFile(Long id, UpdateFileRequest request);

    byte[] downloadFileContent(Long id);

}
