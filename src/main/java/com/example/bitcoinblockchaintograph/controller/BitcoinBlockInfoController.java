package com.example.bitcoinblockchaintograph.controller;

import com.example.bitcoinblockchaintograph.service.BitcoinBlockInfoService;
import com.example.bitcoinblockchaintograph.util.MethodLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/block-info")
@Slf4j
@RequiredArgsConstructor
public class BitcoinBlockInfoController {

    private final BitcoinBlockInfoService service;

    /**
     * Start loading blocks data to graph DB
     *
     * @return ok
     */
    @Operation(summary = "Load blocks info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Start loading blocks info to graph DB")
    })
    @PostMapping("load")
    public ResponseEntity<String> loadNodes() throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        service.loadNotLoadedBlocksInfo();
        return ResponseEntity.ok().build();
    }
}
