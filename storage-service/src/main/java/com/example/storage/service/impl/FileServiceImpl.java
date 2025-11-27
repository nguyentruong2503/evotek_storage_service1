package com.example.storage.service.impl;

import com.example.common.model.dto.FileDTO;
import com.example.common.model.request.FileSearchRequest;
import com.example.common.model.request.UpdateFileRequest;
import com.example.storage.builder.FileSearchBuilder;
import com.example.storage.converter.FileConverter;
import com.example.storage.converter.FileSearchBuilderConverter;
import com.example.storage.entity.FileEntity;
import com.example.common.exception.*;
import com.example.storage.repository.FileRepository;
import com.example.storage.service.CacheService;
import com.example.storage.service.CloudinaryService;
import com.example.storage.service.FileService;
import com.example.storage.specification.FileSpecification;
import com.example.storage.util.CloudinaryUtils;
import com.example.storage.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;


import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileSearchBuilderConverter fileSearchBuilderConverter;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private CacheService cacheService;

    @Override
    public Page<FileDTO> getAll(FileSearchRequest fileSearchRequest, int page, int size) {
        FileSearchBuilder fileSearchBuilder = fileSearchBuilderConverter.toFileSearchBuilder(fileSearchRequest);
        Specification<FileEntity> spec = FileSpecification.filter(fileSearchBuilder);
        Page<FileEntity> entityPage = fileRepository.findAll(spec, PageRequest.of(page - 1, size));
        return entityPage.map(fileConverter::toFileDTO);
    }

    @Override
    public List<FileDTO> uploadMultipleFiles(MultipartFile[] files, Boolean isPublic,Long ownerID) throws Exception {
        List<FileDTO> results = new ArrayList<>();

        for (MultipartFile file : files) {

            String hash = FileUtils.md5Hash(file);
            if (fileRepository.existsByHash(hash)) {
                System.out.println("File " + file.getOriginalFilename() + " đã tồn tại");
                continue; // bỏ qua file trùng
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);

            FileEntity entity = new FileEntity();
            entity.setName(file.getOriginalFilename());
            entity.setSize(BigInteger.valueOf(file.getSize()));
            entity.setType(file.getContentType());
            entity.setUrl((String) uploadResult.get("secure_url"));
            entity.setIs_public(isPublic);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setOwner_id(ownerID);
            entity.setHash(hash);

            FileEntity saved = fileRepository.save(entity);
            results.add(fileConverter.toFileDTO(saved));
        }
        return results;
    }

    @Override
    public FileDTO getFileById(Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));

        return fileConverter.toFileDTO(fileEntity);
    }

    @Override
    public FileDTO getPublicFile(Long id) throws AccessDeniedException {
        String key = "file:public:" + id;
        FileDTO cachedFile = cacheService.get(key,FileDTO.class);
        if(cachedFile !=null){
            System.out.println(">>> CACHE HIT " + key);
            return cachedFile;
        }
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));

        if(fileEntity.getIs_public()){
            FileDTO fileDTO = fileConverter.toFileDTO(fileEntity);
            cacheService.set(key,fileDTO,180L);
            return fileDTO;
        }
        throw new AccessDeniedException("Bạn không có quyền xem file này");
    }

    @Override
    public String getPrivateViewUrl(Long id, Integer width, Integer height, Double ratio,Long ownerID) throws AccessDeniedException {
        String key = "file:private:" + id;
        FileDTO cachedFile = cacheService.get(key,FileDTO.class);
        if(cachedFile !=null){
            System.out.println(">>> CACHE HIT " + key);
            return CloudinaryUtils.buildCloudinaryUrl(cachedFile.getUrl(), width, height, ratio);
        }

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));


        if (!file.getType().startsWith("image")) {
            throw new RuntimeException("Chỉ hỗ trợ xem ảnh");
        }

        if(!file.getIs_public()){
            FileDTO fileDTO = fileConverter.toFileDTO(file);
            cacheService.set(key,fileDTO,180L);
            return CloudinaryUtils.buildCloudinaryUrl(file.getUrl(), width, height, ratio);
        }

        throw new AccessDeniedException("Đây là file public");
    }

    @Override
    public String getPublicViewUrl(Long id, Integer width, Integer height, Double ratio) throws AccessDeniedException {
        String key = "file:public:" + id;
        FileDTO cachedFile = cacheService.get(key,FileDTO.class);
        if(cachedFile !=null){
            System.out.println(">>> CACHE HIT " + key);
            return CloudinaryUtils.buildCloudinaryUrl(cachedFile.getUrl(), width, height, ratio);
        }

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));


        if (!file.getType().startsWith("image")) {
            throw new RuntimeException("Chỉ hỗ trợ xem ảnh (image/*)");
        }

        if(file.getIs_public()){
            FileDTO fileDTO = fileConverter.toFileDTO(file);
            cacheService.set(key,fileDTO,180L);
            return CloudinaryUtils.buildCloudinaryUrl(file.getUrl(), width, height, ratio);
        }

        throw new AccessDeniedException("Bạn không có quyền xem file này");
    }

    @Override
    public void deleteFile(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));
        file.setDeleted(true);
        fileRepository.save(file);
        deleteFileCache(id);
    }

    @Override
    public FileDTO updateFile(Long id, UpdateFileRequest request) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));

        if (request.getName() != null) file.setName(request.getName());
        if (request.getIsPublic() != null) file.setIs_public(request.getIsPublic());
        if (request.getOwnerId() != null) file.setOwner_id(request.getOwnerId());
        file.setUpdatedAt(LocalDateTime.now());

        FileEntity saved = fileRepository.save(file);
        deleteFileCache(id);
        return fileConverter.toFileDTO(saved);
    }

    @Override
    public byte[] downloadFileContent(Long id) {
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("File không tồn tại"));

        try (InputStream in = new URL(file.getUrl()).openStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file từ URL", e);
        }
    }

    private void deleteFileCache(Long id){
        String key = "file:public" + id;
        String key2 = "file:private" + id;
        cacheService.delete(key);
        cacheService.delete(key2);
    }
}
