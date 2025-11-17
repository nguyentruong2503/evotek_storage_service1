package com.example.iam2.service.impl;

import com.example.iam2.builder.UserBuilder;
import com.example.iam2.converter.UserConverter;
import com.example.iam2.converter.UserBuilderConverter;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.exception.DuplicateException;
import com.example.iam2.exception.InvalidTokenException;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.PasswordDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.request.UserExcelDTO;
import com.example.iam2.model.request.UserExportRequest;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.service.KeycloakClientCredentialsService;
import com.example.iam2.service.KeycloakService;
import com.example.iam2.service.UserService;
import com.example.iam2.specification.UserSpecification;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JWTServiceImpl jwtService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserBuilderConverter userBuilderConverter;

    @Autowired
    private JwtDecoder keycloakJwtDecoder;

    @Value("${iam.security.keycloak-enabled:false}")
    private boolean keycloakEnabled;

    @Value("${default.password}")
    private String defaultPassw;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Value("${keycloak.server-url}")
    private String keycloakServer;

    @Value("${keycloak.realm}")
    private String realms;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private KeycloakClientCredentialsService keycloakClientCredentialsService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public UserDTO create(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateException("Email đã tồn tại");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new DuplicateException("Số điện đã tồn tại");
        }

        UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);
        RoleEntity defaultRole= roleRepository.findByCode("ROLE_USER");
        userEntity.setRoles(Collections.singleton(defaultRole));
        userEntity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userEntity.setLocked(false);
        userEntity.setDeleted(false);

        userRepository.save(userEntity);
        return userDTO;
    }

    public UserProfile findUserById(String token) {
        Long userIdFromToken;

        try {
            if (keycloakEnabled) {
                // decode token Keycloak, lấy preferred_username, xuống DB để lấy thông tin
                String username = keycloakJwtDecoder.decode(token).getClaimAsString("preferred_username");
                UserEntity userEntity = userRepository.findByUsernameAndLockedAndDeleted(username, false, false)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
                return modelMapper.map(userEntity,UserProfile.class);
            } else {
                // decode token jwt
                userIdFromToken = jwtService.getUserIdFromToken(token);
                UserEntity userEntity = userRepository.findById(userIdFromToken)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));
                return modelMapper.map(userEntity,UserProfile.class);
            }
        } catch (ParseException e) {
            throw new InvalidTokenException("Token không hợp lệ");
        }
    }

    @Override
    public void lockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setLocked(true);
        userRepository.save(userEntity);
    }

    @Override
    public void unlockUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setLocked(false);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setDeleted(true);
        userRepository.save(userEntity);
    }

    @Override
    public void resetPassword(Long id) {
        UserEntity userEntity = userRepository.findById(id).get();
        userEntity.setPassword(passwordEncoder.encode(defaultPassw));
        userRepository.save(userEntity);
    }

    @Override
    public void changePassword(String token, PasswordDTO passwordDTO) {

        String username = keycloakJwtDecoder.decode(token).getClaimAsString("preferred_username");
        String userId = keycloakJwtDecoder.decode(token).getClaimAsString("sub");

        boolean checkOldPass = validateOldPassword(username,passwordDTO.getCurrentPassword());
        if(!checkOldPass){
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        resetPasswordByAdmin(userId,passwordDTO.getNewPassword());
    }

    @Override
    public Page<UserDTO> searchUsers(UserExportRequest request, int page, int size) {
        UserBuilder builder = userBuilderConverter.toUserBuilder(request, request.getRoles());
        Specification<UserEntity> spec = UserSpecification.filter(builder);
        Page<UserEntity> entityPage = userRepository.findAll(spec, PageRequest.of(page - 1, size));
        return entityPage.map(userConverter::toUserDTO);
    }

    @Override
    public UserDetail userDetail(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
        UserDetail userDetail = modelMapper.map(userEntity, UserDetail.class);
        List<RoleDTO> roleDTOs = userEntity.getRoles().stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toList());
        userDetail.setRoles(roleDTOs);
        return userDetail;
    }

    @Override
    public void assignRoleToUser(AssignRoleDTO assignRoleDTO) {
        UserEntity userEntity= userRepository.findById(assignRoleDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User không tồn tại"));
        List<RoleEntity> newRoles = roleRepository.findByIdIn(assignRoleDTO.getRoleIds());
        userEntity.getRoles().addAll(newRoles);
        userRepository.save(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrantedAuthority> getAuthoritiesByUsername(String username) {
        UserEntity user = userRepository.findByUsernameWithRolesAndPermissions(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        for (RoleEntity role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
            role.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.getName())));
        }
        return new ArrayList<>(authorities);
    }

    @Override
    public List<UserExcelDTO> importUsers(InputStream excelFile) throws Exception {
        List<UserExcelDTO> users = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        int rowNum = 0;
        for (Row row : sheet) {
            rowNum++;
            if (rowNum == 1) continue;

            UserExcelDTO dto = new UserExcelDTO();
            try {
                dto.setStt((int) row.getCell(0).getNumericCellValue());
                dto.setUsername(getCellStringValue(row.getCell(1)));
                dto.setEmail(getCellStringValue(row.getCell(2)));
                dto.setFirstName(getCellStringValue(row.getCell(3)));
                dto.setLastName(getCellStringValue(row.getCell(4)));

                String dobStr = getCellStringValue(row.getCell(5));
                if (dobStr != null) {
                    try {
                        dto.setBirthday(dateFormatter.parse(dobStr));
                    } catch (ParseException e) {
                        dto.getErrors().add("Ngày sinh không hợp lệ");
                    }
                }

                dto.setPhone(getCellStringValue(row.getCell(6)));
                dto.setStreet(getCellStringValue(row.getCell(7)));
                dto.setWard(getCellStringValue(row.getCell(8)));
                dto.setDistrict(getCellStringValue(row.getCell(9)));
                dto.setProvince(getCellStringValue(row.getCell(10)));

                Cell yearsCell = row.getCell(11);
                if (yearsCell != null) {
                    if (yearsCell.getCellType() == CellType.NUMERIC) {
                        dto.setYearsOfEx((int) yearsCell.getNumericCellValue());
                    } else {
                        dto.getErrors().add("Số năm kinh nghiệm không hợp lệ");
                    }
                }

            } catch (Exception e) {
                dto.getErrors().add("Lỗi đọc dữ liệu: " + e.getMessage());
            }

            // Validate
            if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
                dto.getErrors().add("Username trống");
            }
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                dto.getErrors().add("Email trống");
            } else if (!dto.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                dto.getErrors().add("Email không hợp lệ");
            }

            if (dto.getPhone() == null || dto.getPhone().isEmpty()) {
                dto.getErrors().add("Số điện thoại trống");
            }

            if (dto.getFirstName() == null || dto.getFirstName().isEmpty()) {
                dto.getErrors().add("Họ tên trống");
            }
            if (dto.getLastName() == null || dto.getLastName().isEmpty()) {
                dto.getErrors().add("Họ tên trống");
            }

            if (userRepository.existsByUsername(dto.getUsername())) {
                dto.getErrors().add("Username đã tồn tại");
            }
            if (userRepository.existsByEmail(dto.getEmail())) {
                dto.getErrors().add("Email đã tồn tại");
            }
            if (userRepository.existsByPhone(dto.getPhone())) {
                dto.getErrors().add("Số điện thoại đã tồn tại");
            }

            users.add(dto);
        }

        workbook.close();
        saveValidUsers(users);
        return users;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
    @Transactional
    public void saveValidUsers(List<UserExcelDTO> users) {
        for (UserExcelDTO dto : users) {
            if (!dto.getErrors().isEmpty()) continue;

            UserDTO userDTO =modelMapper.map(dto,UserDTO.class);
            userDTO.setPassword(defaultPassw);
            keycloakService.register(userDTO);
        }
    }

    @Override
    public ByteArrayInputStream exportUsers(UserExportRequest request) throws IOException {
        List<String> roles = request.getRoles();
        UserBuilder builder = userBuilderConverter.toUserBuilder(request,roles);
        Specification<UserEntity> spec = UserSpecification.filter(builder);
        List<UserEntity> users = userRepository.findAll(spec);

        Map<String, Object> beans = new HashMap<>();
        beans.put("users", users);
        beans.put("dateFormat", new SimpleDateFormat("dd/MM/yyyy"));

        Context context = new Context(beans);
        try (InputStream is = getClass().getResourceAsStream("/templates/users_template.xlsx");
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            JxlsHelper.getInstance().processTemplate(is, os, context);

            return new ByteArrayInputStream(os.toByteArray());
        }
    }

    public boolean validateOldPassword(String username, String oldPassword) {
        String url = tokenUri;
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", oldPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            if (response != null && response.get("access_token") != null) {
                System.out.println(response);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void resetPasswordByAdmin(String userId, String newPassword) {
        String adminToken = keycloakClientCredentialsService.getAccessToken();

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> payload = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        restTemplate.put(
                keycloakServer + "/admin/realms/"+ realms + "/users/" + userId + "/reset-password",
                request
        );
    }

}
