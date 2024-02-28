package com.example.shoppingmall.shop.Dto;

import com.example.shoppingmall.shop.entity.ShopCategory;
import com.example.shoppingmall.shop.entity.ShopStatus;
import lombok.Data;

@Data
public class ShopDto {
    private String name;
    private String description;
    private ShopCategory category;
    private ShopStatus status;
    private Long ownerId;
}
