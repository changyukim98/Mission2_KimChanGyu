package com.example.shoppingmall.useditem.dto;

import com.example.shoppingmall.useditem.entity.ProposalStatus;
import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProposalDto {
    private Long id;

    @Setter
    private Long itemId;
    @Setter
    private Long proposerId;
    @Setter
    private ProposalStatus status;

    public static ProposalDto fromEntity(PurchaseProposal entity) {
        return ProposalDto.builder()
                .id(entity.getId())
                .itemId(entity.getItem().getId())
                .proposerId(entity.getProposer().getId())
                .status(entity.getStatus())
                .build();
    }
}
