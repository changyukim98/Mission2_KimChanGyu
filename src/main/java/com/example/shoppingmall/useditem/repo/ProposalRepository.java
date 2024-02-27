package com.example.shoppingmall.useditem.repo;

import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalRepository
    extends JpaRepository<PurchaseProposal, Long> {

    List<PurchaseProposal> findAllByItemId(Long itemId);

    List<PurchaseProposal> findAllByItemIdAndProposerUsername(Long item_id, String name);
}
