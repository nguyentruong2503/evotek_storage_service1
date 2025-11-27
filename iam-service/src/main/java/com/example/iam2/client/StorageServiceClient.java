package com.example.iam2.client;

import com.example.common.model.dto.FileDTO;
import com.example.common.model.request.FileSearchRequest;
import com.example.common.model.request.UpdateFileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "storage-service",
        configuration = FeignConfig.class
)
public interface StorageServiceClient {

    @GetMapping("/api/file/public/{id}")
    FileDTO getPublicFile(@PathVariable("id") Long id);

    @GetMapping("/api/file/private/{id}")
    FileDTO getPrivateFile(@PathVariable("id") Long id);

    @GetMapping("/api/file/private/view/{id}")
    ResponseEntity<String> viewPrivateFile(@PathVariable("id") Long id,
                                           @RequestParam(value = "width", required = false) Integer width,
                                           @RequestParam(value = "height", required = false) Integer height,
                                           @RequestParam(value = "ratio", required = false) Double ratio,
                                           @RequestParam("owner_id") Long ownerId);

    @GetMapping("/api/file/public/view/{id}")
    ResponseEntity<String> viewPublicFile(@PathVariable("id") Long id,
                                          @RequestParam(value = "width", required = false) Integer width,
                                          @RequestParam(value = "height", required = false) Integer height,
                                          @RequestParam(value = "ratio", required = false) Double ratio);

    @PostMapping(value = "/api/file/private/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<FileDTO> uploadPrivateFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("is_public") Boolean isPublic,
            @RequestParam("owner_id") Long ownerId
    );

    @PostMapping(value = "/api/file/public/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<FileDTO> uploadPublicFiles(
            @RequestPart("files") MultipartFile[] files,
            @RequestPart("is_public") Boolean isPublic,
            @RequestParam("owner_id") Long ownerId
    );

    @DeleteMapping("/api/file/{id}")
    void deleteFile(@PathVariable("id") Long id);

    @PostMapping("/api/file/search")
    Page<FileDTO> searchFiles(@RequestBody FileSearchRequest request,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "5") int size);

    @PutMapping("/api/file/{id}")
    FileDTO updateFile(@PathVariable("id") Long id,
                       @RequestBody UpdateFileRequest request);

    @GetMapping("/api/file/download/{id}")
    ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id);
}



