package com.example.shoppingmall.useditem.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.useditem.dto.ProposalDto;
import com.example.shoppingmall.useditem.entity.ItemStatus;
import com.example.shoppingmall.useditem.entity.ProposalStatus;
import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import com.example.shoppingmall.useditem.entity.UsedItem;
import com.example.shoppingmall.useditem.repo.ProposalRepository;
import com.example.shoppingmall.useditem.repo.UsedItemRepository;
import com.example.shoppingmall.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final UsedItemRepository itemRepository;
    private final AuthenticationFacade facade;

    public ProposalDto acceptProposal(Long proposalId) {
        Optional<PurchaseProposal> optionalProposal = proposalRepository.findById(proposalId);
        // 제안이 존재하지 않을 경우
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        PurchaseProposal proposal = optionalProposal.get();
        UsedItem item = proposal.getItem();
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 제안서의 아이템의 소유자만 수락 가능
        if (!item.getUser().getId().equals(currentUser.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        proposal.setStatus(ProposalStatus.ACCEPTED);
        return ProposalDto.fromEntity(proposalRepository.save(proposal));
    }

    public ProposalDto declineProposal(Long proposalId) {
        Optional<PurchaseProposal> optionalProposal = proposalRepository.findById(proposalId);
        // 제안이 존재하지 않을 경우
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        PurchaseProposal proposal = optionalProposal.get();
        UsedItem item = proposal.getItem();
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 제안서의 아이템의 소유자만 거절 가능
        if (!item.getUser().getId().equals(currentUser.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        proposal.setStatus(ProposalStatus.DECLINED);
        return ProposalDto.fromEntity(proposalRepository.save(proposal));
    }

    @Transactional
    public ProposalDto confirmAcceptedProposal(Long proposalId) {
        Optional<PurchaseProposal> optionalProposal = proposalRepository.findById(proposalId);
        // 제안이 존재하지 않을 경우
        if (optionalProposal.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        PurchaseProposal proposal = optionalProposal.get();
        UsedItem item = proposal.getItem();
        UserEntity currentUser = facade.getCurrentUserEntity();

        // 제안서의 제안자만 확정 가능
        if (!proposal.getProposer().getId().equals(currentUser.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 제안의 상태가 ACCEPTED가 아닐 경우
        if (!proposal.getStatus().equals(ProposalStatus.ACCEPTED))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // 아이템의 상태를 판매 완료로
        item.setStatus(ItemStatus.SOLD_OUT);
        itemRepository.save(item);

        // 다른 모든 제안을 거절로 만듬
        proposalRepository.setAllProposalsStatusForItem(ProposalStatus.DECLINED, item.getId());
        proposal.setStatus(ProposalStatus.CONFIRMED);
        return ProposalDto.fromEntity(proposalRepository.save(proposal));
    }
}
