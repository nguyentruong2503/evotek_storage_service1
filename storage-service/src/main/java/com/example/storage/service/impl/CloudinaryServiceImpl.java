package com.example.storage.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.storage.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.tika.Tika;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được rỗng");
        }

        List<String> allowedMimeTypes = List.of(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/x-tika-ooxml",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-powerpoint",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "text/plain",
                "text/csv",
                "audio/mpeg",
                "video/mp4",
                "application/zip",
                "application/x-rar-compressed",
                "image/jpeg",
                "image/jpg",
                "image/png"
        );

        // Danh sách extension cho phép
        List<String> allowedExtensions = List.of(
                ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
                ".txt", ".csv", ".mp3", ".mp4", ".zip", ".rar",
                ".jpg", ".jpeg", ".png"
        );

        try {
            // Dùng Tika kiểm tra MIME type thực tế
            Tika tika = new Tika();
            String detectedType = tika.detect(file.getInputStream());
            if (!allowedMimeTypes.contains(detectedType)) {
                throw new RuntimeException("File không được phép: " + detectedType);
            }

            // Kiểm tra extension
            String filename = file.getOriginalFilename();
            if (filename == null || allowedExtensions.stream().noneMatch(ext -> filename.toLowerCase().endsWith(ext))) {
                throw new RuntimeException("File không hợp lệ: " + filename);
            }

            String resourceType;
            if (detectedType.startsWith("image")) {
                resourceType = "image";
            } else if (detectedType.startsWith("video")) {
                resourceType = "video";
            } else {
                resourceType = "raw";
            }
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", resourceType
                    )
            );
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi upload lên Cloudinary", ex);
        }
    }
}
