package com.example.shoppingmall.shop.shopitem.controller;

import com.example.shoppingmall.shop.shopitem.dto.ShopItemOrderRequest;
import com.example.shoppingmall.shop.shopitem.dto.ShopItemOrderResponse;
import com.example.shoppingmall.shop.shopitem.dto.ShopItemRequest;
import com.example.shoppingmall.shop.shopitem.dto.ShopItemResponse;
import com.example.shoppingmall.shop.shopitem.service.ShopItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/shop/item")
@RequiredArgsConstructor
public class ShopItemController {
    private final ShopItemService shopItemService;

    @PostMapping
    public ShopItemResponse registerShopItem(
            @RequestPart("image")
            MultipartFile image,
            @RequestPart("dto")
            ShopItemRequest dto
    ) {
        return shopItemService.registerShopItem(image, dto);
    }

    @PutMapping("/{itemId}")
    public ShopItemResponse updateShopItem(
            @PathVariable("itemId")
            Long itemId,
            @RequestPart("image")
            MultipartFile image,
            @RequestPart("dto")
            ShopItemRequest dto
    ) {
        return shopItemService.updateShopItem(itemId, image, dto);
    }

    @DeleteMapping("/{itemId}")
    public String deleteShopItem(
            @PathVariable("itemId")
            Long itemId
    ) {
        shopItemService.deleteShopItem(itemId);
        return "done";
    }

    @PostMapping("/{itemId}/order")
    public ShopItemOrderResponse orderShopItem(
            @PathVariable("itemId")
            Long itemId,
            @RequestBody
            ShopItemOrderRequest request
    ) {
        return shopItemService.orderShopItem(itemId, request);
    }
}
