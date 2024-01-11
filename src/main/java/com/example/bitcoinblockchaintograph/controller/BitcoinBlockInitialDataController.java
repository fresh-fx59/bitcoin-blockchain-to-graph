package com.example.bitcoinblockchaintograph.controller;

import com.example.bitcoinblockchaintograph.service.BitcoinBlockInitialDataService;
import com.example.bitcoinblockchaintograph.util.MethodLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/block-initial-data")
@Slf4j
@RequiredArgsConstructor
public class BitcoinBlockInitialDataController {

    private final BitcoinBlockInitialDataService service;

    @PostMapping("load-data")
    public ResponseEntity<String> loadData() throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        service.loadBitcoinBlockInitialData();
        return ResponseEntity.ok().build();
    }
}
