package com.example.shoppingmall.shop.controller;

import com.example.shoppingmall.shop.Dto.ShopRegDto;
import com.example.shoppingmall.shop.Dto.ShopRegResponseDto;
import com.example.shoppingmall.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("shop")
public class ShopController {
    private final ShopService shopService;

    @PostMapping
    public ShopRegResponseDto registerShop(
            @RequestBody
            ShopRegDto dto
    ) {
        return shopService.registerShop(dto);
    }

    @GetMapping
    public List<ShopRegResponseDto> readAllShopReg() {
        return shopService.readAllShopReg();
    }
}
