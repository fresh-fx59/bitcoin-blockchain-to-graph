package com.example.bitcoinblockchaintograph.controller;

import com.example.bitcoinblockchaintograph.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockService;
import com.example.bitcoinblockchaintograph.util.MethodLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.consensusj.bitcoin.json.pojo.BlockChainInfo;
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
     * Show block data
     *
     * @return retrieved BitcoinBlockDTO
     */
    @Operation(summary = "Get block from DB")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получить блок из БД")
    })
    @GetMapping("/db/{blockHash}")
    public ResponseEntity<BitcoinBlockDTO> getBlockFromDb(@PathVariable String blockHash) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.getBlockFromDb(blockHash));
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

    /**
     * Show BlockChainInfo
     *
     * @return BlockChainInfo
     */
    @Operation(summary = "Get BlockChain Info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get BlockChain Info")
    })
    @GetMapping("info")
    public ResponseEntity<BlockChainInfo> getBlockChainInfo() throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.getBlockChainInfo());
    }

    /**
     * Show saved block data
     *
     * @return saved BitcoinBlockDTO
     */
    @Operation(summary = "Save block by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get block from blockchain and save it to db")
    })
    @PostMapping("id/{id}")
    public ResponseEntity<BitcoinBlockDTO> saveBlockFromClientToDb(@PathVariable Integer id) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.saveBlockFromClientToDb(id));
    }

    /**
     * Enrich block with previous block
     *
     * @return ok
     */
    @Operation(summary = "Enrich block")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrich block")
    })
    @PostMapping("enrich/hash/{hash}")
    public ResponseEntity<BitcoinBlockDTO> enrichBlock(@PathVariable String hash) throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        return ResponseEntity.ok(service.enrichBlock(hash));
    }

    /**
     * Start loading blocks data to graph DB
     *
     * @return ok
     */
    @Operation(summary = "Load blocks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Start loading blocks data to graph DB")
    })
    @PostMapping("load-nodes")
    public ResponseEntity<String> loadNodes() throws Exception {
        log.info("REST request to " + MethodLogger.getMethodName());
        service.loadNotLoadedBlocks();
        return ResponseEntity.ok().build();
    }

//    @PostMapping("load-nodes")
//    public ResponseEntity<String> loadNodes() throws Exception {
//        log.info("REST request to " + MethodLogger.getMethodName());
//        return ResponseEntity.ok(service.loadNodes());
//    }
}
