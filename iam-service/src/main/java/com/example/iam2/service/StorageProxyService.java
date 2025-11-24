package com.example.iam2.service;

import com.example.common.exception.NotFoundException;
import com.example.iam2.client.StorageServiceClient;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.repository.UserRepository;
import com.example.common.model.dto.FileDTO;
import com.example.common.model.dto.UserDTO;
import com.example.common.model.request.FileSearchRequest;
import com.example.common.model.request.UpdateFileRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StorageProxyService {

    @Autowired
    private StorageServiceClient storageServiceClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtDecoder keycloakJwtDecoder;

    @Autowired
    private HttpServletRequest request;

    // Lấy current user  từ JWT
    private UserDTO getCurrentUserEntity() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new RuntimeException("Missing Authorization header");
        }

        String username = keycloakJwtDecoder.decode(token)
                .getClaimAsString("preferred_username");

        UserEntity userEntity = userRepository.findByUsernameAndLockedAndDeleted(username, false, false)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
        return modelMapper.map(userEntity, UserDTO.class);
    }

    public FileDTO getPublicFile(Long id) {
        return storageServiceClient.getPublicFile(id);
    }

    public FileDTO getPrivateFile(Long id) {
        return storageServiceClient.getPrivateFile(id);
    }


    public List<FileDTO> uploadPrivateFiles(MultipartFile[] files) {
        UserDTO currentUser = getCurrentUserEntity();
        Long ownerId = currentUser.getId();
        return storageServiceClient.uploadPrivateFiles(files,false,ownerId);
    }

    public List<FileDTO> uploadPublicFiles(MultipartFile[] files) {
        UserDTO currentUser = getCurrentUserEntity();
        Long ownerId = currentUser.getId();
        return storageServiceClient.uploadPublicFiles(files,true,ownerId);
    }


    public String viewPrivateFile(Long id, Integer width, Integer height, Double ratio){
        UserDTO currentUser = getCurrentUserEntity();
        Long ownerId = currentUser.getId();
        return storageServiceClient.viewPrivateFile(id, width, height, ratio, ownerId).getBody();
    }

    public String viewPublicFile(Long id, Integer width, Integer height, Double ratio){
        UserDTO currentUser = getCurrentUserEntity();
        Long ownerId = currentUser.getId();
        return storageServiceClient.viewPublicFile(id, width, height, ratio).getBody();
    }


    public void deleteFile(Long id) {
        storageServiceClient.deleteFile(id);
    }

    public Page<FileDTO> searchFiles(FileSearchRequest req, int page, int size) {
        return storageServiceClient.searchFiles(req, page, size);
    }

    public FileDTO updateFile(UpdateFileRequest updateFileRequest, Long id){
        return storageServiceClient.updateFile(id,updateFileRequest);
    }

    public ResponseEntity<byte[]> downloadFile(Long id) {
        return storageServiceClient.downloadFile(id);
    }
}
