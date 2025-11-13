package com.example.iam2.service.impl;

import com.example.iam2.builder.UserExportBuilder;
import com.example.iam2.converter.UserConverter;
import com.example.iam2.converter.UserExportBuilderConverter;
import com.example.iam2.entity.RoleEntity;
import com.example.iam2.entity.UserEntity;
import com.example.iam2.exception.DuplicateException;
import com.example.iam2.exception.InvalidTokenException;
import com.example.iam2.exception.NotFoundException;
import com.example.iam2.model.dto.AssignRoleDTO;
import com.example.iam2.model.dto.RoleDTO;
import com.example.iam2.model.dto.UserDTO;
import com.example.iam2.model.request.UserExcelDTO;
import com.example.iam2.model.request.UserExportRequest;
import com.example.iam2.model.response.PagedResponse;
import com.example.iam2.model.response.UserDetail;
import com.example.iam2.model.response.UserProfile;
import com.example.iam2.repository.RoleRepository;
import com.example.iam2.repository.UserRepository;
import com.example.iam2.service.KeycloakService;
import com.example.iam2.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private UserExportBuilderConverter userExportBuilderConverter;

    @Autowired
    private JwtDecoder keycloakJwtDecoder;

    @Value("${iam.security.keycloak-enabled:false}")
    private boolean keycloakEnabled;

    @Autowired
    private KeycloakService keycloakService;

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
        userEntity.setRoles(Collections.singletonList(defaultRole));
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
        userEntity.setPassword(passwordEncoder.encode("123456aA@"));
        userRepository.save(userEntity);
    }

    @Override
    public PagedResponse<UserDTO> getAllUsers(int page, int size) {
        List<UserEntity> entities = userRepository.getAll(page, size);
        long totalItems = userRepository.countAll();
        List<UserDTO> dtos = entities.stream()
                .map(userConverter::toUserDTO)
                .toList();

        return new PagedResponse<>(dtos, page, size, totalItems);
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
            if (rowNum == 1) continue; // skip header

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

            // Validate bắt buộc
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
            userDTO.setPassword("123456");
            keycloakService.register(userDTO);
        }
    }

    @Override
    public ByteArrayInputStream exportUsers(UserExportRequest request) throws IOException {
        UserExportBuilder builder = userExportBuilderConverter.toUserExportBuilder(request);

        List<UserEntity> users = userRepository.importByFilter(builder);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontName("Times New Roman");

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);

        Font dataFont = workbook.createFont();
        dataFont.setFontName("Times New Roman");

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setWrapText(true);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);

        String[] headers = {
                "STT", "Username", "Email", "First Name", "Last Name", "Date of Birth",
                "Phone", "Street", "Ward", "District", "Province", "Years of Experience",
                "Locked", "Deleted"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerCellStyle);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int rowNum = 1;
        for (UserEntity user : users) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(rowNum - 1); // STT
            row.createCell(1).setCellValue(Optional.ofNullable(user.getUsername()).orElse(""));
            row.createCell(2).setCellValue(Optional.ofNullable(user.getEmail()).orElse(""));
            row.createCell(3).setCellValue(Optional.ofNullable(user.getFirstName()).orElse(""));
            row.createCell(4).setCellValue(Optional.ofNullable(user.getLastName()).orElse(""));
            row.createCell(5).setCellValue(
                    user.getBirthday() != null ? dateFormat.format(user.getBirthday()) : "");
            row.createCell(6).setCellValue(Optional.ofNullable(user.getPhone()).orElse(""));
            row.createCell(7).setCellValue(Optional.ofNullable(user.getStreet()).orElse(""));
            row.createCell(8).setCellValue(Optional.ofNullable(user.getWard()).orElse(""));
            row.createCell(9).setCellValue(Optional.ofNullable(user.getDistrict()).orElse(""));
            row.createCell(10).setCellValue(Optional.ofNullable(user.getProvince()).orElse(""));
            row.createCell(11).setCellValue(
                    user.getYearsOfEx() != null ? user.getYearsOfEx() : 0);
            row.createCell(12).setCellValue(
                    user.getLocked() != null && user.getLocked() ? "Yes" : "No");
            row.createCell(13).setCellValue(
                    user.getDeleted() != null && user.getDeleted() ? "Yes" : "No");

            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
