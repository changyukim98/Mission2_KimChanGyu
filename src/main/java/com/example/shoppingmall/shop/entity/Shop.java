package com.example.shoppingmall.shop.entity;

import com.example.shoppingmall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private String description;
    @Setter
    @Enumerated(EnumType.STRING)
    private ShopCategory category;
    @Setter
    @Enumerated(EnumType.STRING)
    private ShopStatus status;

    @OneToOne
    private UserEntity owner;
}
