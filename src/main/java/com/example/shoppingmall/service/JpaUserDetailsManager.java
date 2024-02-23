package com.example.shoppingmall.service;

import com.example.shoppingmall.entity.CustomUserDetails;
import com.example.shoppingmall.entity.UserEntity;
import com.example.shoppingmall.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepository userRepository;

    public JpaUserDetailsManager(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;

        createUser(CustomUserDetails.builder()
                .username("admin")
                .password(passwordEncoder.encode("1234"))
                .role("ROLE_ADMIN")
                .build());
    }

    @Override
    public void createUser(UserDetails user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        try {
            CustomUserDetails userDetail = (CustomUserDetails) user;
            UserEntity newUser = UserEntity.builder()
                    .username(userDetail.getUsername())
                    .password(userDetail.getPassword())
                    .role(userDetail.getRole())
                    .build();
            userRepository.save(newUser);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        try {
            CustomUserDetails userDetail = (CustomUserDetails) user;
            UserEntity userEntity = UserEntity.builder()
                    .id(userDetail.getId())
                    .username(userDetail.getUsername())
                    .password(userDetail.getPassword())
                    .nickname(userDetail.getNickname())
                    .firstName(userDetail.getFirstName())
                    .lastName(userDetail.getLastName())
                    .age(userDetail.getAge())
                    .email(userDetail.getEmail())
                    .phone(userDetail.getPhone())
                    .profileImagePath(userDetail.getProfileImagePath())
                    .role(userDetail.getRole())
                    .businessNum(userDetail.getBusinessNum())
                    .build();
            userRepository.save(userEntity);
        } catch (ClassCastException e) {
            log.error("Failed Cast to: {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);

        UserEntity userEntity = optionalUser.get();
        return CustomUserDetails.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .nickname(userEntity.getNickname())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .age(userEntity.getAge())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .profileImagePath(userEntity.getProfileImagePath())
                .role(userEntity.getRole())
                .businessNum(userEntity.getBusinessNum())
                .build();
    }

    public UserEntity loadUserEntityByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);
        return optionalUser.get();
    }
}
