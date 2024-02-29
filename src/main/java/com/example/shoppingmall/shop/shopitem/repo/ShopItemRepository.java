package com.example.shoppingmall.shop.shopitem.repo;

import com.example.shoppingmall.shop.shopitem.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopItemRepository
    extends JpaRepository<ShopItem, Long> {
}
