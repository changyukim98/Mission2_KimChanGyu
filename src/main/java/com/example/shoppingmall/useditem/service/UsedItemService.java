package com.example.shoppingmall.useditem.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.useditem.dto.ProposalDto;
import com.example.shoppingmall.useditem.dto.UsedItemDto;
import com.example.shoppingmall.useditem.entity.ItemStatus;
import com.example.shoppingmall.useditem.entity.ProposalStatus;
import com.example.shoppingmall.useditem.entity.PurchaseProposal;
import com.example.shoppingmall.useditem.repo.ProposalRepository;
import com.example.shoppingmall.useditem.repo.UsedItemRepository;
import com.example.shoppingmall.user.UserRole;
import com.example.shoppingmall.user.entity.CustomUserDetails;
import com.example.shoppingmall.useditem.entity.UsedItem;
import com.example.shoppingmall.user.entity.UserEntity;
import com.example.shoppingmall.user.repo.UserRepository;
import com.example.shoppingmall.user.service.JpaUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsedItemService {
    private final UsedItemRepository usedItemRepository;
    private final UserRepository userRepository;
    private final ProposalRepository proposalRepository;
    private final JpaUserDetailsManager manager;
    private final AuthenticationFacade facade;

    public UsedItemDto createUsedItem(UsedItemDto dto) {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (!userDetails.getRole().equals(UserRole.ROLE_USER))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UsedItem usedItem = UsedItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .user(UserEntity.fromUserDetails(userDetails))
                .status(ItemStatus.ON_SALE)
                .build();
        return UsedItemDto.fromEntity(usedItemRepository.save(usedItem));
    }

    public List<UsedItemDto> readAllUsedItem() {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (userDetails.getRole().equals(UserRole.ROLE_INACTIVE))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return usedItemRepository.findAll().stream()
                .map(UsedItemDto::fromEntity)
                .toList();
    }

    public UsedItemDto updateUsedItem(Long id, UsedItemDto dto) {
        Optional<UsedItem> optionalItem = usedItemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        UsedItem usedItem = optionalItem.get();
        if (!usedItem.getUser().getId().equals(userDetails.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        usedItem.setTitle(dto.getTitle());
        usedItem.setDescription(dto.getDescription());
        usedItem.setPrice(dto.getPrice());
        return UsedItemDto.fromEntity(usedItemRepository.save(usedItem));
    }

    public void deleteUsedItem(Long id) {
        Optional<UsedItem> optionalItem = usedItemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        UsedItem usedItem = optionalItem.get();
        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        if (!usedItem.getUser().getUsername().equals(userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        usedItemRepository.deleteById(id);
    }

    public ProposalDto createProposal(Long itemId) {
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        // item이 존재하지 않을 경우
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        // 비활성회원은 구매 제안 불가
        if (userDetails.getRole().equals(UserRole.ROLE_INACTIVE))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UsedItem usedItem = optionalItem.get();
        // 등록자와 구매자가 같은 경우 구매 제안 불가
        if (usedItem.getUser().getUsername().equals(userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UserEntity proposer = manager.loadUserEntityByUsername(userDetails.getUsername());
        PurchaseProposal proposal = PurchaseProposal.builder()
                .item(usedItem)
                .proposer(proposer)
                .status(ProposalStatus.WAITING)
                .build();

        return ProposalDto.fromEntity(proposalRepository.save(proposal));
    }

    public List<ProposalDto> readProposals(Long itemId) {
        Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
        // 아이템이 존재하지 않는 경우
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // 비활성회원은 조회 불가
        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        if (userDetails.getRole().equals(UserRole.ROLE_INACTIVE))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UsedItem usedItem = optionalItem.get();
        UserEntity viewer =
                manager.loadUserEntityByUsername(userDetails.getUsername());

        // 조회자가 아이템의 소유자인 경우
        if (usedItem.getUser().getUsername().equals(viewer.getUsername())) {
            return proposalRepository.findAllByItemId(itemId).stream()
                    .map(ProposalDto::fromEntity)
                    .toList();
        } else {
            return proposalRepository.findAllByItemIdAndProposerUsername(itemId, viewer.getUsername())
                    .stream()
                    .map(ProposalDto::fromEntity)
                    .toList();
        }
    }
}
