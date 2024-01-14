package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.config.BitcoinPeer;
import com.example.bitcoinblockchaintograph.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlock;
import com.example.bitcoinblockchaintograph.exception.BitcoinBlockException;
import com.example.bitcoinblockchaintograph.mapper.BitcoinBlockMapper;
import com.example.bitcoinblockchaintograph.repository.neo4j.BitcoinBlockRepository;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockInitialDataService;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockService;
import com.example.bitcoinblockchaintograph.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.Block;
import org.consensusj.bitcoin.json.pojo.BlockChainInfo;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus.HEADER_SAVED;
import static com.example.bitcoinblockchaintograph.util.BatchTool.getBatchedLists;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinBlockServiceImpl implements BitcoinBlockService {
    @Value("${batch.parallelism}")
    private int parallelism;

    private final BitcoinBlockMapper mapper;
    private final BitcoinBlockRepository repository;
    private final BitcoinBlockInitialDataService bitcoinBlockInitialDataService;
    private final BitcoinClient client;
    private final TransactionalService transactionalService;

    @Override
    public void loadNotLoadedBlocks() {
        List<String> hashesToLoad = new ArrayList<>(bitcoinBlockInitialDataService.getNotLoadedHashes());
        Set<String> loadedHashes = repository.findAllBlocksHashes();

        Collection<String> intersectionOfHashes = CollectionUtils.intersection(hashesToLoad, loadedHashes);

        if (!CollectionUtils.isEmpty(intersectionOfHashes)) {
            hashesToLoad.removeAll(loadedHashes);
            bitcoinBlockInitialDataService.updateBlocksStatus(intersectionOfHashes, HEADER_SAVED);
        }

        int batchSize = 1000;

        if (CollectionUtils.isEmpty(hashesToLoad))
            return;

        List<List<String>> batchedHashes = getBatchedLists(hashesToLoad, batchSize);

        try (ExecutorService customThreadPool = Executors.newFixedThreadPool(parallelism)) {
            customThreadPool.submit(() -> batchedHashes.parallelStream().forEach(this::processHashes));
        }
    }

    private void processHashes(Collection<String> hashes) {
        List<BitcoinBlock> listOfBlocks = new ArrayList<>();
        String firstHashInBlocks = hashes.stream()
                .findFirst()
                .orElseThrow(() -> new BitcoinBlockException("Failed to process BitcoinBlock. Hashes are empty."));

        hashes.forEach(hash -> {
            try {
                Block block = client.getBlock(Sha256Hash.wrap(hash));
                listOfBlocks.add(mapper.toEntity(block));
            } catch (IOException e) {
                throw new BitcoinBlockException("Failed to process BitcoinBlock hashes " + firstHashInBlocks);
            }
        });

        transactionalService.saveBitcoinBlocksAndUpdateStatuses(listOfBlocks, hashes);

        log.info("Batch {} done", firstHashInBlocks);
    }

    @Override
    public BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception {
        // Retrieve a block through a peer
        Sha256Hash blockHash = Sha256Hash.wrap(blockHashParam);
        try (BitcoinPeer bitcoinPeer = new BitcoinPeer()) {
            Future<Block> future = bitcoinPeer.getPeer().getBlock(blockHash);
            log.info("Waiting for node to send us the requested block: " + blockHash);
            Block block = future.get();
            return mapper.toDto(block);
        }
    }

    @Override
    public BitcoinBlockDTO getBlockFromDb(String blockHashParam) throws Exception {
        // Retrieve a block through a peer
        Sha256Hash blockHash = Sha256Hash.wrap(blockHashParam);
        Optional<BitcoinBlock> blockFromRepo = repository.findOneByBlockHash(blockHash.toString());

        return blockFromRepo.map(mapper::toDto).orElse(null);
    }

    @Override
    public BlockChainInfo getBlockChainInfo() throws Exception {
        // Retrieve a block through a peer
        return client.getBlockChainInfo();
    }

    @Override
    public BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception {        // Retrieve a block through a peer
        BitcoinBlockDTO blockDto = getBlockFromPeer(blockHashParam);
        BitcoinBlock block = mapper.toEntity(blockDto);

        return mapper.toDto(repository.save(block));
    }

    @Override
    public BitcoinBlockDTO saveBlockFromClientToDb(Integer id) throws Exception {
        Sha256Hash blockHash = client.getBlockHash(id);
        String blockHashString = blockHash.toString();

        Optional<BitcoinBlock> blockFromRepo = repository.findOneByBlockHash(blockHashString);

        if (blockFromRepo.isPresent())
            return mapper.toDto(blockFromRepo.get());

        Block block = client.getBlock(blockHash);
        BitcoinBlock bitcoinBlock = mapper.toEntity(block);
        return mapper.toDto(repository.save(bitcoinBlock));
    }

    @Override
    public BitcoinBlockDTO enrichBlock(String blockHash) throws Exception {
        BitcoinBlock initialBlock = saveAndGetBitcoinBlockByHash(blockHash);

        BitcoinBlock previousBlock = saveAndGetBitcoinBlockByHash(initialBlock.getPreviousBlockHash());

        initialBlock.setPreviousBlock(previousBlock);

        return mapper.toDto(initialBlock);
    }

    private BitcoinBlock saveAndGetBitcoinBlockByHash(String blockHash) throws IOException {
        Optional<BitcoinBlock> blockFromRepo = repository.findOneByBlockHash(blockHash);

        return blockFromRepo.orElseGet(() -> {
            try {
                return saveAndGetBlock(blockHash);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private BitcoinBlock saveAndGetBlock(String blockHash) throws IOException {
        Block block = client.getBlock(Sha256Hash.wrap(blockHash));
        BitcoinBlock bitcoinBlock = mapper.toEntity(block);
        return repository.save(bitcoinBlock);
    }

    @Override
    public String loadNodes() throws IOException {
        Integer blocksCount = client.getBlockChainInfo().getBlocks();
        Integer maxBlockNumberFromDb = repository.findMaxBlockNumber();
        int startingBlock = maxBlockNumberFromDb != null ? maxBlockNumberFromDb + 1 : 0;

        if (maxBlockNumberFromDb != null) {
            if (blocksCount.compareTo(maxBlockNumberFromDb) == 0) {
                return "All blocks are loaded";
            } else if (blocksCount.compareTo(maxBlockNumberFromDb) < 0) {
                return "Something went wrong blocksCount " + blocksCount + " is less than maxBlockNumberFromDb " + maxBlockNumberFromDb;
            }
        }

        for (int i = startingBlock; i <= blocksCount; i++) {
            String hash = String.valueOf(client.getBlockHash(i));
            Block block;
            try {
                block = client.getBlock(Sha256Hash.wrap(hash));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BitcoinBlock bitcoinBlock = mapper.toEntity(block);
            bitcoinBlock.setBlockNumber(i);
            repository.save(bitcoinBlock);
        }

        return "done";
    }
}
