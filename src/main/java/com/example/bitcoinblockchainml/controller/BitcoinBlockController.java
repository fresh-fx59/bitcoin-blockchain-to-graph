package com.example.bitcoinblockchainml.controller;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.service.BitcoinBlockService;
import com.example.bitcoinblockchainml.service.BitcoinService;
import com.example.bitcoinblockchainml.util.MethodLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/block")
@Slf4j
@RequiredArgsConstructor
public class BitcoinBlockController {

    private final BitcoinBlockService service;

    /**
     * Show block data
     *
     * @return version string
     */
    @Operation(summary = "Получить блок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получить блок из блокчейна")
    })
    @GetMapping("{blockHash}")
    public ResponseEntity<BitcoinBlockDTO> getBlock(@PathVariable String blockHash) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.downloadBlock(blockHash));
    }
}
