package com.example.shoppingmall.shop.shopitem.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.shop.entity.Shop;
import com.example.shoppingmall.shop.repo.ShopRepository;
import com.example.shoppingmall.shop.shopitem.dto.ShopItemRequest;
import com.example.shoppingmall.shop.shopitem.dto.ShopItemResponse;
import com.example.shoppingmall.shop.shopitem.entity.ShopItem;
import com.example.shoppingmall.shop.shopitem.repo.ShopItemRepository;
import com.example.shoppingmall.user.entity.UserEntity;
import com.example.shoppingmall.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopItemService {
    private final ShopItemRepository shopItemRepository;
    private final ShopRepository shopRepository;
    private final AuthenticationFacade facade;

    public ShopItemResponse registerShopItem(
            MultipartFile image,
            ShopItemRequest itemDto
    ) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 비지니스 사용자만 아이템 등록가능
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<Shop> optionalShop = shopRepository.findByOwnerId(currentUser.getId());
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        Shop shop = optionalShop.get();

        // 이미지 저장
        String imagePath = saveItemImage(image);

        ShopItem shopItem = ShopItem.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .price(itemDto.getPrice())
                .imagePath(imagePath)
                .stock(itemDto.getStock())
                .shop(shop)
                .build();

        return ShopItemResponse.fromEntity(shopItemRepository.save(shopItem));
    }

    public ShopItemResponse updateShopItem(
            Long itemId,
            MultipartFile image,
            ShopItemRequest itemDto
    ) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 비지니스 사용자만 아이템 수정 가능
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ShopItem> optionalItem = shopItemRepository.findById(itemId);
        // 존재하지 않는 item일 경우 잘못된 요청
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        ShopItem shopItem = optionalItem.get();

        Optional<Shop> optionalShop = shopRepository.findByOwnerId(currentUser.getId());
        // shop이 존재하지 않으면 에러
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        Shop shop = optionalShop.get();

        // 자신의 상점의 상품이 아닐 경우
        if (!shopItem.getShop().getId().equals(shop.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 기존 이미지 삭제
        deleteFile(shopItem.getImagePath());
        // 새 이미지 저장
        String imagePath = saveItemImage(image);

        shopItem.setName(itemDto.getName());
        shopItem.setDescription(itemDto.getDescription());
        shopItem.setPrice(itemDto.getPrice());
        shopItem.setImagePath(imagePath);
        shopItem.setStock(itemDto.getStock());

        return ShopItemResponse.fromEntity(shopItemRepository.save(shopItem));
    }

    public void deleteShopItem(Long itemId) {
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 비지니스 사용자만 아이템 삭제 가능
        if (!currentUser.getRole().equals(UserRole.ROLE_BUSINESS))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Optional<ShopItem> optionalItem = shopItemRepository.findById(itemId);
        // 존재하지 않는 item일 경우 잘못된 요청
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        ShopItem shopItem = optionalItem.get();

        Optional<Shop> optionalShop = shopRepository.findByOwnerId(currentUser.getId());
        // shop이 존재하지 않으면 에러
        if (optionalShop.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        Shop shop = optionalShop.get();

        // 자신의 상점의 상품이 아닐 경우
        if (!shopItem.getShop().getId().equals(shop.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 이미지 삭제
        deleteFile(shopItem.getImagePath());

        shopItemRepository.deleteById(itemId);
    }

    public String saveItemImage(MultipartFile image) {
        String itemImageDir = "media/item/";

        // 폴더 만들기
        try {
            Files.createDirectories(Path.of(itemImageDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 파일 이름
        String originalFilename = image.getOriginalFilename();
        String imageFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 파일 경로
        String imagePath = itemImageDir + imageFilename;
        // 저장
        try {
            image.transferTo(Path.of(imagePath));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return imagePath;
    }

    public void deleteFile(String filePath) {
        try {
            Files.delete(Path.of(filePath));
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
