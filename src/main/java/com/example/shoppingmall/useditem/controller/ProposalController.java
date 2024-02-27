package com.example.shoppingmall.useditem.controller;

import com.example.shoppingmall.useditem.dto.ProposalDto;
import com.example.shoppingmall.useditem.service.ProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/proposal")
public class ProposalController {
    private final ProposalService proposalService;

    @PostMapping("/{id}/accept")
    public ProposalDto acceptProposal(
            @PathVariable("id")
            Long proposalId
    ) {
        return proposalService.acceptProposal(proposalId);
    }

    @PostMapping("/{id}/decline")
    public ProposalDto declineProposal(
            @PathVariable("id")
            Long proposalId
    ) {
        return proposalService.declineProposal(proposalId);
    }

    @PostMapping("/{id}/confirm")
    public ProposalDto confirmAcceptedProposal(
            @PathVariable("id")
            Long proposalId
    ) {
        return proposalService.confirmAcceptedProposal(proposalId);
    }
}
