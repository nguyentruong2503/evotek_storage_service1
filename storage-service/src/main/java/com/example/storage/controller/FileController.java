package com.example.storage.controller;

import com.example.storage.model.dto.FileDTO;
import com.example.storage.model.request.FileSearchRequest;
import com.example.storage.model.request.UpdateFileRequest;
import com.example.storage.model.response.PagedResponse;
import com.example.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/public/upload")
    public ResponseEntity<List<FileDTO>> uploadPublicFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("owner_id") Long ownerId) {
        List<FileDTO> result = fileService.uploadMultipleFiles(files, true,ownerId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<FileDTO> getPublicFile(@PathVariable Long id) throws AccessDeniedException {
        return ResponseEntity.ok(fileService.getPublicFile(id));
    }

    @GetMapping("/public/view/{id}")
    public ResponseEntity<String> viewPublicFile(
            @PathVariable Long id,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) Double ratio) throws AccessDeniedException {

        String transformedUrl = fileService.getPublicViewUrl(id, width, height, ratio);
        return ResponseEntity.ok(transformedUrl);
    }

    @GetMapping("/private/view/{id}")
    @PreAuthorize("hasAuthority('FILE_VIEW_PRIVATE')")
    public ResponseEntity<String> viewPrivateFile(
            @PathVariable Long id,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) Double ratio,
            @RequestParam("owner_id") Long ownerId) throws AccessDeniedException {

        String transformedUrl = fileService.getPrivateViewUrl(id, width, height, ratio, ownerId);
        return ResponseEntity.ok(transformedUrl);
    }

    @PostMapping("/private/upload")
    @PreAuthorize("hasAuthority('FILE_UPLOAD')")
    public ResponseEntity<List<FileDTO>> uploadPrivateFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("owner_id") Long ownerId) {
        List<FileDTO> result = fileService.uploadMultipleFiles(files, false,ownerId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/private/{id}")
    @PreAuthorize("hasAuthority('FILE_VIEW_PRIVATE')")
    public ResponseEntity<FileDTO> getPrivateFile(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.getFileById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FILE_DELETE')")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("status", "success", "message", "Xóa thành công!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FILE_UPDATE')")
    public ResponseEntity<FileDTO> updateFile(
            @PathVariable Long id,
            @RequestBody UpdateFileRequest request) {
        FileDTO updated = fileService.updateFile(id, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasAuthority('FILE_DOWNLOAD')")
    public ResponseEntity<byte[]> downloadPrivateFile(@PathVariable Long id) {

        FileDTO file = fileService.getFileById(id);
        byte[] data = fileService.downloadFileContent(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, file.getType())
                .body(data);
    }

    @PostMapping("/search")
    public PagedResponse<FileDTO> searchFiles(
            @RequestBody FileSearchRequest fileSearchRequest,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        return fileService.getAll(fileSearchRequest, page, size);
    }
}
