package com.example.shoppingmall.useditem.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.useditem.repo.UsedItemRepository;
import com.example.shoppingmall.useditem.dto.UsedItemDto;
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
    private final JpaUserDetailsManager manager;
    private final AuthenticationFacade facade;

    public UsedItemDto createUsedItem(UsedItemDto dto) {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (!userDetails.getRole().equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UsedItem usedItem = UsedItem.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .user(UserEntity.fromUserDetails(userDetails))
                .build();
        return UsedItemDto.fromEntity(usedItemRepository.save(usedItem));
    }

    public List<UsedItemDto> readAllUsedItem() {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (userDetails.getRole().equals("ROLE_INACTIVE"))
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
}
