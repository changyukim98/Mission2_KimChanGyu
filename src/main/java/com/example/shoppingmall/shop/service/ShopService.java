package com.example.shoppingmall.shop.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.shop.Dto.ShopRegDto;
import com.example.shoppingmall.shop.Dto.ShopRegResponseDto;
import com.example.shoppingmall.shop.entity.ShopRegStatus;
import com.example.shoppingmall.shop.entity.ShopRegistration;
import com.example.shoppingmall.shop.repo.ShopRegRepository;
import com.example.shoppingmall.shop.repo.ShopRepository;
import com.example.shoppingmall.user.entity.UserEntity;
import com.example.shoppingmall.user.entity.UserRole;
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
    private final AuthenticationFacade facade;

    public ShopRegResponseDto registerShop(ShopRegDto dto) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 사업자 사용자만 등록 가능
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        ShopRegistration shopReg = ShopRegistration.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .owner(currentUser)
                .status(ShopRegStatus.WAITING)
                .build();
        return ShopRegResponseDto.fromEntity(shopRegRepository.save(shopReg));
    }

    public List<ShopRegResponseDto> readAllShopReg() {
        UserEntity currentUser = facade.getCurrentUserEntity();

        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return shopRegRepository.findAll().stream()
                .map(ShopRegResponseDto::fromEntity)
                .toList();
    }
}