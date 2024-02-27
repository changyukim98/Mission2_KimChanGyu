package com.example.shoppingmall.useditem.repo;

import com.example.shoppingmall.useditem.entity.ProposalStatus;
import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalRepository
        extends JpaRepository<PurchaseProposal, Long> {

    // 소유자가 자신의 아이템에 걸린 제안을 확인하는 메서드
    List<PurchaseProposal> findAllByItemId(Long itemId);

    // 구매자가 자신의 제안을 확인하는 메서드
    List<PurchaseProposal> findAllByItemIdAndProposerUsername(Long itemId, String name);

    @Modifying
    @Query("UPDATE PurchaseProposal p " +
            "SET p.status = :status " +
            "WHERE p.item.id = :itemId")
    void setAllProposalsStatusForItem(
            @Param("status") ProposalStatus status,
            @Param("itemId") Long itemId
    );
}
