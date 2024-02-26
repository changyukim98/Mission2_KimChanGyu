package com.example.shoppingmall.service;

import com.example.shoppingmall.dto.EssentialInfoDto;
import com.example.shoppingmall.dto.LoginDto;
import com.example.shoppingmall.dto.RegisterDto;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
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
        CustomUserDetails userDetails = getCurrentUserDetails();

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
        CustomUserDetails userDetails = getCurrentUserDetails();

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
    public void businessRegister(String businessNum) {
        CustomUserDetails userDetails = getCurrentUserDetails();

        // ROLE_USER에서만 사업자 유저로 업그레이드 신청 가능
        if (!userDetails.getRole().equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        BusinessRegistration businessRegistration
                = BusinessRegistration.builder()
                .user(userDetailsManager.loadUserEntityByUsername(userDetails.getUsername()))
                .businessNum(businessNum)
                .build();
        businessRepository.save(businessRegistration);
    }

    public List<BusinessRegistration> readBusinessRegistration() {
        CustomUserDetails userDetails = getCurrentUserDetails();

        // ROLE_ADMIN에서만 조회 가능
        if (!userDetails.getRole().equals("ROLE_ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return businessRepository.findAll();
    }

    @Transactional
    public void acceptBusinessRegistration(Long id) {
        CustomUserDetails userDetails = getCurrentUserDetails();

        // ROLE_ADMIN에서만 승인 가능
        if (!userDetails.getRole().equals("ROLE_ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 가입 신청이 존재하지 않는 경우
        Optional<BusinessRegistration> optionalRegistration = businessRepository.findById(id);
        if (optionalRegistration.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        BusinessRegistration registration = optionalRegistration.get();
        UserEntity userEntity = registration.getUser();

        // 일반 유저일 경우에만 업그레이드 가능
        if (!userEntity.getRole().equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        userEntity.setRole("ROLE_BUSINESS");
        userRepository.save(userEntity);
        businessRepository.delete(registration);
    }

    public void declineBusinessRegistration(Long id) {
        CustomUserDetails userDetails = getCurrentUserDetails();

        // ROLE_ADMIN에서만 거절 가능
        if (!userDetails.getRole().equals("ROLE_ADMIN"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 가입 신청이 존재하지 않는 경우
        if (!businessRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        businessRepository.deleteById(id);
    }

    public CustomUserDetails getCurrentUserDetails() {
        String username
                = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return (CustomUserDetails) userDetailsManager.loadUserByUsername(username);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
