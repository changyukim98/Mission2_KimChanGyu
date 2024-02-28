package com.example.shoppingmall.shop.Dto;

import com.example.shoppingmall.shop.entity.ShopCategory;
import com.example.shoppingmall.shop.entity.ShopRegStatus;
import com.example.shoppingmall.shop.entity.ShopRegistration;
import com.example.shoppingmall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Data
@Builder
public class ShopRegResponseDto {
    private Long id;
    private String name;
    private String description;
    private ShopCategory category;
    private ShopRegStatus status;
    private String declineReason;
    private Long ownerId;

    public static ShopRegResponseDto fromEntity(ShopRegistration entity) {
        return ShopRegResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .declineReason(entity.getDeclineReason())
                .ownerId(entity.getOwner().getId())
                .build();
    }
}
