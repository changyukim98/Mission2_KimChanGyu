package com.example.shoppingmall.useditem.entity;

import com.example.shoppingmall.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UsedItem item;

    @ManyToOne
    private UserEntity proposer;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProposalStatus status;
}
