package com.example.shoppingmall.service;

import com.example.shoppingmall.dto.EssentialInfoDto;
import com.example.shoppingmall.dto.LoginDto;
import com.example.shoppingmall.dto.RegisterDto;
import com.example.shoppingmall.dto.UserDto;
import com.example.shoppingmall.entity.BusinessRegistration;
import com.example.shoppingmall.entity.CustomUserDetails;
import com.example.shoppingmall.entity.UserEntity;
import com.example.shoppingmall.jwt.JwtTokenUtils;
import com.example.shoppingmall.jwt.JwtResponseDto;
import com.example.shoppingmall.repo.BusinessRepository;
import com.example.shoppingmall.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserDetailsManager userDetailsManager;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;

    // 유저 계정 생성
    public void createUser(RegisterDto registerDto) {
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role("ROLE_INACTIVE")
                .build();
        userDetailsManager.createUser(userDetails);
    }

    // 로그인 정보를 바탕으로 토큰 생성
    public JwtResponseDto loginUser(LoginDto loginDto) {
        if (!userDetailsManager.userExists(loginDto.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UserDetails userDetails
                = userDetailsManager.loadUserByUsername(loginDto.getUsername());

        if (!passwordEncoder.matches(loginDto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponseDto(token);
    }

    // 필수 정보 입력
    public void fillEssential(EssentialInfoDto dto) {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        CustomUserDetails userDetails
                = (CustomUserDetails) userDetailsManager.loadUserByUsername(username);

        userDetails.setNickname(dto.getNickname());
        userDetails.setFirstName(dto.getFirstName());
        userDetails.setLastName(dto.getLastName());
        userDetails.setAge(dto.getAge());
        userDetails.setEmail(dto.getEmail());
        userDetails.setPhone(dto.getPhone());
        if (userDetails.getRole().equals("ROLE_INACTIVE"))
            userDetails.setRole("ROLE_USER");
        userDetailsManager.updateUser(userDetails);
    }

    public void updateProfileImage(String profileImagePath) {
        String username =
                SecurityContextHolder.getContext().getAuthentication().getName();
        CustomUserDetails userDetails
                = (CustomUserDetails) userDetailsManager.loadUserByUsername(username);

        userDetails.setProfileImagePath(profileImagePath);
        userDetailsManager.updateUser(userDetails);
    }

    public String saveImage(MultipartFile image) {
        String profileDir = "media/profile/";

        // 폴더 만들기
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 유저 이름 가져오기
        String username
                = SecurityContextHolder.getContext().getAuthentication().getName();

        // 파일 이름
        String originalFilename = image.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = username + "." + extension;

        // 파일 경로
        String profilePath = profileDir + profileFilename;
        // 저장
        try {
            image.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return profilePath;
    }

    @Transactional
    public void upgradeToBusiness(String businessNum) {
        String username
                = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomUserDetails userDetails
                = (CustomUserDetails) userDetailsManager.loadUserByUsername(username);

        // ROLE_USER에서만 사업자 유저로 업그레이드 가능
        if (!userDetails.getRole().equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        BusinessRegistration businessRegistration
                = BusinessRegistration.builder()
                .user(userDetailsManager.loadUserEntityByUsername(username))
                .build();
        businessRepository.save(businessRegistration);

        userDetails.setBusinessNum(businessNum);
        userDetails.setRole("ROLE_BUSINESS");
        userDetailsManager.updateUser(userDetails);
    }

    public List<UserEntity> readBusinessRegistration() {
        String username
                = SecurityContextHolder.getContext().getAuthentication().getName();
        CustomUserDetails userDetails
                = (CustomUserDetails) userDetailsManager.loadUserByUsername(username);

        // ROLE_ADMIN에서만 조회 가능
        if (!userDetails.getRole().equals("ROLE_ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return businessRepository.findAll().stream()
                .map(BusinessRegistration::getUser)
                .toList();

    }
}
