package com.example.storage.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.storage.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.folder}")
    private String folder;

    @Override
    public Map<String, Object> uploadFile(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            String resourceType;

            if (contentType != null && contentType.startsWith("image")) {
                resourceType = "image";
            } else if (contentType != null && contentType.startsWith("video")) {
                resourceType = "video";
            } else {
                resourceType = "raw"; // cho PDF, DOCX, ZIP, ...
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
