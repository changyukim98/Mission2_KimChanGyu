package com.example.shoppingmall.shop.repo;

import com.example.shoppingmall.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository
    extends JpaRepository<Shop, Long> {
}
