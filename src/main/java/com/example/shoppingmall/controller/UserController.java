package com.example.shoppingmall.controller;

import com.example.shoppingmall.dto.EssentialInfoDto;
import com.example.shoppingmall.dto.LoginDto;
import com.example.shoppingmall.dto.RegisterDto;
import com.example.shoppingmall.entity.BusinessRegistration;
import com.example.shoppingmall.entity.UserEntity;
import com.example.shoppingmall.jwt.JwtResponseDto;
import com.example.shoppingmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public JwtResponseDto userLogin(
            @RequestBody
            LoginDto dto
    ) {
        return userService.loginUser(dto);
    }

    @GetMapping("/current-user")
    public String test() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/register")
    public String userRegister(
            @RequestBody
            RegisterDto dto
    ) {
        userService.createUser(dto);
        return "done";
    }

    @PostMapping("/fill-essential")
    public String fillEssential(
            @RequestBody
            EssentialInfoDto dto
    ) {
        userService.fillEssential(dto);
        return "done";
    }

    @PostMapping("/update-avatar")
    public String updateAvatar(
            @RequestParam("image")
            MultipartFile image
    ) {
        String profilePath = userService.saveImage(image);
        userService.updateProfileImage(profilePath);
        return "done";
    }

    @PostMapping("/business")
    public String businessRegister(
            @RequestParam("business-number")
            String businessNumber
    ) {
        userService.businessRegister(businessNumber);
        return "done";
    }

    @GetMapping("/business")
    public List<BusinessRegistration> readBusinessRegistrations() {
        return userService.readBusinessRegistration();
    }

    @PostMapping("/business/{id}/accept")
    public String acceptBusinessRegistration(
            @PathVariable("id")
            Long id
    ) {
        userService.acceptBusinessRegistration(id);
        return "done";
    }

    @PostMapping("/business/{id}/decline")
    public String declineBusinessRegistration(
            @PathVariable("id")
            Long id
    ) {
        userService.declineBusinessRegistration(id);
        return "done";
    }
}
