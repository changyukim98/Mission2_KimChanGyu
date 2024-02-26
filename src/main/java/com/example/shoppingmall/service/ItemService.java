package com.example.shoppingmall.service;

import com.example.shoppingmall.AuthenticationFacade;
import com.example.shoppingmall.dto.ItemDto;
import com.example.shoppingmall.entity.CustomUserDetails;
import com.example.shoppingmall.entity.Item;
import com.example.shoppingmall.entity.UserEntity;
import com.example.shoppingmall.repo.ItemRepository;
import com.example.shoppingmall.repo.UserRepository;
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
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;
    private final AuthenticationFacade facade;

    public ItemDto createItem(ItemDto dto) {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (!userDetails.getRole().equals("ROLE_USER"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        Item item = Item.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .user(UserEntity.fromUserDetails(userDetails))
                .build();
        return ItemDto.fromEntity(itemRepository.save(item));
    }

    public List<ItemDto> readAllItem() {
        CustomUserDetails userDetails = facade.getCurrentUserDetails();

        if (userDetails.getRole().equals("ROLE_INACTIVE"))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return itemRepository.findAll().stream()
                .map(ItemDto::fromEntity)
                .toList();
    }

    public ItemDto updateItem(Long id, ItemDto dto) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        Item item = optionalItem.get();
        if (!item.getUser().getId().equals(userDetails.getId()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        return ItemDto.fromEntity(itemRepository.save(item));
    }

    public void deleteItem(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Item item = optionalItem.get();
        CustomUserDetails userDetails = facade.getCurrentUserDetails();
        if (!item.getUser().getUsername().equals(userDetails.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        itemRepository.deleteById(id);
    }
}
