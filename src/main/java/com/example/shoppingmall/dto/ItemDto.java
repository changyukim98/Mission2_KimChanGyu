package com.example.shoppingmall.dto;

import com.example.shoppingmall.entity.Item;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private Integer price;
    @Setter
    private String imagePath;

    private Long userId;

    public static ItemDto fromEntity(Item entity) {
        return ItemDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imagePath(entity.getImagePath())
                .userId(entity.getUser().getId())
                .build();
    }
}
