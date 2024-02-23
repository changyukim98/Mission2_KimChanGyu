package com.example.shoppingmall.repo;

import com.example.shoppingmall.entity.BusinessRegistration;
import com.example.shoppingmall.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessRepository
    extends JpaRepository<BusinessRegistration, Long> {
}
