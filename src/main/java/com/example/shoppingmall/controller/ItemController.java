package com.example.shoppingmall.controller;

import com.example.shoppingmall.dto.ItemDto;
import com.example.shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @RequestBody
            ItemDto dto

    ) {
        return itemService.createItem(dto);
    }

    @GetMapping
    public List<ItemDto> readAllItem() {
        return itemService.readAllItem();
    }

    @PutMapping("/{id}")
    public ItemDto updateItem(
            @PathVariable("id")
            Long id,
            @RequestBody
            ItemDto dto
    ) {
        return itemService.updateItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteItem(
            @PathVariable("id")
            Long id
    ) {
        itemService.deleteItem(id);
        return "done";
    }
}
