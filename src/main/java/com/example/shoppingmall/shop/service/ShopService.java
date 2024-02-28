package com.example.shoppingmall.shop.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.shop.Dto.ShopDto;
import com.example.shoppingmall.shop.Dto.ShopRegDeclineDto;
import com.example.shoppingmall.shop.Dto.ShopRegDto;
import com.example.shoppingmall.shop.Dto.ShopRegResponseDto;
import com.example.shoppingmall.shop.entity.Shop;
import com.example.shoppingmall.shop.entity.ShopRegStatus;
import com.example.shoppingmall.shop.entity.ShopRegistration;
import com.example.shoppingmall.shop.entity.ShopStatus;
import com.example.shoppingmall.shop.repo.ShopRegRepository;
import com.example.shoppingmall.shop.repo.ShopRepository;
import com.example.shoppingmall.user.entity.UserEntity;
import com.example.shoppingmall.user.entity.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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

        // admin만 조회 가능
        if (currentUser.getRole().equals(UserRole.ROLE_ADMIN)) {
            return shopRegRepository.findAll().stream()
                    .map(ShopRegResponseDto::fromEntity)
                    .toList();
        } else if (currentUser.getRole().equals(UserRole.ROLE_BUSINESS)) {
            return shopRegRepository.findAllByOwnerId(currentUser.getId())
                    .stream()
                    .map(ShopRegResponseDto::fromEntity)
                    .toList();
        } else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ShopDto acceptShopReg(Long regId) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // admin만 허가 가능
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ShopRegistration> optionalReg = shopRegRepository.findById(regId);
        // 등록 신청이 없는 경우
        if (optionalReg.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        ShopRegistration shopReg = optionalReg.get();
        // 이미 처리된 Shop 등록 요청인 경우
        if (!shopReg.getStatus().equals(ShopRegStatus.WAITING))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UserEntity owner = shopReg.getOwner();
        Optional<Shop> optionalShop = shopRepository.findByOwnerId(owner.getId());
        // 행여나 Shop이 존재하지 않을 경우
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        shopReg.setStatus(ShopRegStatus.ACCEPTED);
        shopRegRepository.save(shopReg);

        Shop shop = optionalShop.get();
        shop.setName(shopReg.getName());
        shop.setDescription(shopReg.getDescription());
        shop.setCategory(shopReg.getCategory());
        shop.setStatus(ShopStatus.OPEN);

        return ShopDto.fromEntity(shopRepository.save(shop));
    }

    public ShopRegResponseDto declineShopReg(Long regId, ShopRegDeclineDto dto) {
        UserEntity currentUser = facade.getCurrentUserEntity();
        // admin만 허가 가능
        if (!currentUser.getRole().equals(UserRole.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ShopRegistration> optionalReg = shopRegRepository.findById(regId);
        // 등록 신청이 없는 경우
        if (optionalReg.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        ShopRegistration shopReg = optionalReg.get();
        // 이미 처리된 Shop 등록 요청인 경우
        if (!shopReg.getStatus().equals(ShopRegStatus.WAITING))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        shopReg.setStatus(ShopRegStatus.DECLINED);
        shopReg.setDeclineReason(dto.getReason());
        return ShopRegResponseDto.fromEntity(shopRegRepository.save(shopReg));
    }
}