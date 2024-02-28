package com.example.shoppingmall.shop.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.shop.Dto.ShopDto;
import com.example.shoppingmall.shop.Dto.ShopRegDto;
import com.example.shoppingmall.shop.Dto.ShopRegResponseDto;
import com.example.shoppingmall.shop.entity.ShopRegStatus;
import com.example.shoppingmall.shop.entity.ShopRegistration;
import com.example.shoppingmall.shop.repo.ShopRegRepository;
import com.example.shoppingmall.shop.repo.ShopRepository;
import com.example.shoppingmall.user.entity.CustomUserDetails;
import com.example.shoppingmall.user.entity.UserEntity;
import com.example.shoppingmall.user.entity.UserRole;
import com.example.shoppingmall.user.service.JpaUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRegRepository shopRegRepository;
    private final ShopRepository shopRepository;
    private final JpaUserDetailsManager manager;
    private final AuthenticationFacade facade;

    public ShopRegResponseDto registerShop(ShopRegDto dto) {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        // 사업자 사용자만 등록 가능
        if (!userDetails.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UserEntity userEntity = manager.loadUserEntityByUsername(userDetails.getUsername());

        ShopRegistration shopReg = ShopRegistration.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .owner(userEntity)
                .status(ShopRegStatus.WAITING)
                .build();
        return ShopRegResponseDto.fromEntity(shopRegRepository.save(shopReg));
    }

    public List<ShopRegResponseDto> readAllShopReg() {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (!userDetails.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return shopRegRepository.findAll().stream()
                .map(ShopRegResponseDto::fromEntity)
                .toList();
    }
}