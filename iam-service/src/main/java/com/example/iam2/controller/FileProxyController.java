package com.example.iam2.controller;

import com.example.iam2.client.StorageServiceClient;
import com.example.iam2.model.dto.FileDTO;
import com.example.iam2.model.request.FileSearchRequest;
import com.example.iam2.model.request.UpdateFileRequest;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.service.StorageProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/file")
public class FileProxyController {

    @Autowired
    private StorageProxyService storageProxyService;

    @PostMapping("/private/upload")
    public ResponseEntity<List<FileDTO>> uploadPrivate(@RequestParam("files") MultipartFile[] files) {
        List<FileDTO> uploaded = storageProxyService.uploadPrivateFiles(files);
        return ResponseEntity.ok(uploaded);
    }

    @PostMapping("/public/upload")
    public ResponseEntity<List<FileDTO>> uploadPublic(@RequestParam("files") MultipartFile[] files) {
        List<FileDTO> uploaded = storageProxyService.uploadPublicFiles(files);
        return ResponseEntity.ok(uploaded);
    }

    @GetMapping("/private/{id}")
    public ResponseEntity<FileDTO> getPrivate(@PathVariable Long id) {
        return ResponseEntity.ok(storageProxyService.getPrivateFile(id));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<FileDTO> getPublic(@PathVariable Long id) {
        return ResponseEntity.ok(storageProxyService.getPublicFile(id));
    }

    @GetMapping("/private/view/{id}")
    public ResponseEntity<Void> viewPrivate(@PathVariable Long id,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) {
        String transformedUrl = storageProxyService.viewPrivateFile(id, width, height, ratio);

        return ResponseEntity.status(302)
                .header("Location", transformedUrl)
                .build();
    }

    @GetMapping("/public/view/{id}")
    public ResponseEntity<Void> viewPublic(@PathVariable Long id,
                                            @RequestParam(required = false) Integer width,
                                            @RequestParam(required = false) Integer height,
                                            @RequestParam(required = false) Double ratio) {
        String transformedUrl = storageProxyService.viewPublicFile(id, width, height, ratio);

        return ResponseEntity.status(302)
                .header("Location", transformedUrl)
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrivate(@PathVariable Long id) {
        storageProxyService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<PagedResponse<FileDTO>> search(@RequestBody FileSearchRequest req,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(storageProxyService.searchFiles(req, page, size));
    }

    @PutMapping("/{id}")
    public FileDTO updateFile(@PathVariable Long id, @RequestBody UpdateFileRequest request) {

        return storageProxyService.updateFile(request, id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadPrivateFile(@PathVariable Long id) {
        return storageProxyService.downloadFile(id);
    }
}


