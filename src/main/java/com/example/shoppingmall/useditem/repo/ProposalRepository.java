package com.example.shoppingmall.useditem.repo;

import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository
    extends JpaRepository<PurchaseProposal, Long> {
}
