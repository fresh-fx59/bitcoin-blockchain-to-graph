package com.example.bitcoinblockchainml.controller;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.service.BitcoinBlockService;
import com.example.bitcoinblockchainml.util.MethodLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/block")
@Slf4j
@RequiredArgsConstructor
public class BitcoinBlockController {

    private final BitcoinBlockService service;

    /**
     * Show block data
     *
     * @return retrieved BitcoinBlockDTO
     */
    @Operation(summary = "Получить блок")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получить блок из блокчейна")
    })
    @GetMapping("{blockHash}")
    public ResponseEntity<BitcoinBlockDTO> getBlock(@PathVariable String blockHash) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.getBlockFromPeer(blockHash));
    }

    /**
     * Show saved block data
     *
     * @return saved BitcoinBlockDTO
     */
    @Operation(summary = "Save block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get block from blockchain and save it to db")
    })
    @PostMapping("{blockHash}")
    public ResponseEntity<BitcoinBlockDTO> saveBlock(@PathVariable String blockHash) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.saveBlockFromPeerToDb(blockHash));
    }
}
