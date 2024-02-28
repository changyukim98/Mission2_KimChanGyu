package com.example.shoppingmall.shop.repo;

import com.example.shoppingmall.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository
    extends JpaRepository<Shop, Long> {
    Optional<Shop> findByOwnerId(Long id);
}
