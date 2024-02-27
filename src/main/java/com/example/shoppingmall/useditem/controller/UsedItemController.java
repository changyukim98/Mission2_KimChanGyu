package com.example.shoppingmall.useditem.controller;

import com.example.shoppingmall.useditem.dto.ProposalDto;
import com.example.shoppingmall.useditem.dto.UsedItemDto;
import com.example.shoppingmall.useditem.service.UsedItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
public class UsedItemController {
    private final UsedItemService usedItemService;

    @PostMapping
    public UsedItemDto createUsedItem(
            @RequestBody
            UsedItemDto dto
    ) {
        return usedItemService.createUsedItem(dto);
    }

    @GetMapping
    public List<UsedItemDto> readAllItem() {
        return usedItemService.readAllUsedItem();
    }

    @PutMapping("/{id}")
    public UsedItemDto updateUsedItem(
            @PathVariable("id")
            Long id,
            @RequestBody
            UsedItemDto dto
    ) {
        return usedItemService.updateUsedItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteUsedItem(
            @PathVariable("id")
            Long id
    ) {
        usedItemService.deleteUsedItem(id);
        return "done";
    }

    @PostMapping("/{id}/proposal")
    public ProposalDto createProposal(
            @PathVariable("id")
            Long itemId
    ) {
        return usedItemService.createProposal(itemId);
    }

    @GetMapping("/{id}/proposal")
    public List<ProposalDto> readProposal(
            @PathVariable("id")
            Long itemId
    ) {
        return usedItemService.readProposals(itemId);
    }
}
