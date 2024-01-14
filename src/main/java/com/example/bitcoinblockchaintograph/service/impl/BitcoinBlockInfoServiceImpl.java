package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlockInfo;
import com.example.bitcoinblockchaintograph.exception.BitcoinBlockException;
import com.example.bitcoinblockchaintograph.mapper.BitcoinBlockInfoMapper;
import com.example.bitcoinblockchaintograph.repository.neo4j.BitcoinBlockInfoRepository;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockInfoService;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockInitialDataService;
import com.example.bitcoinblockchaintograph.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.bitcoinj.base.Sha256Hash;
import org.consensusj.bitcoin.json.pojo.BlockInfo;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus.HEADER_SAVED;
import static com.example.bitcoinblockchaintograph.util.BatchTool.getBatchedLists;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinBlockInfoServiceImpl implements BitcoinBlockInfoService {
    @Value("${batch.parallelism}")
    private int parallelism;
    private int batchSize = 1000;

    private final BitcoinBlockInfoMapper mapper;
    private final BitcoinBlockInfoRepository repository;
    private final BitcoinBlockInitialDataService bitcoinBlockInitialDataService;
    private final BitcoinClient client;
    private final TransactionalService transactionalService;

    @Override
    public void loadNotLoadedBlocksInfo() {
        List<String> hashesToLoad = new ArrayList<>(bitcoinBlockInitialDataService.getNotLoadedHashes());
        List<Sha256Hash> loadedHashes256 = new ArrayList<>();
        try {
            List<Sha256Hash> hashesToAdd = repository.findAllBlocksHashes();
            loadedHashes256.addAll(hashesToAdd);
        } catch (InvalidDataAccessApiUsageException ignored) {}

        Set<String> loadedHashes = CollectionUtils.isEmpty(loadedHashes256) ?
                new HashSet<>()
                : loadedHashes256.stream().map(Sha256Hash::toString).collect(Collectors.toSet());

        Collection<String> intersectionOfHashes = CollectionUtils.intersection(hashesToLoad, loadedHashes);

        if (!CollectionUtils.isEmpty(intersectionOfHashes)) {
            hashesToLoad.removeAll(loadedHashes);
            bitcoinBlockInitialDataService.updateBlocksStatus(intersectionOfHashes, HEADER_SAVED);
        }

        if (CollectionUtils.isEmpty(hashesToLoad)) {
            log.info("Nothing to load. hashesToLoad is empty");
            return;
        }

        List<List<String>> batchedHashes = getBatchedLists(hashesToLoad, batchSize);

        try (ExecutorService customThreadPool = Executors.newFixedThreadPool(parallelism)) {
            customThreadPool.submit(() -> batchedHashes.parallelStream().forEach(this::processHashes));
        }
    }

    private void processHashes(Collection<String> hashes) {
        List<BitcoinBlockInfo> listOfBlocks = new ArrayList<>();
        String firstHashInBlocks = hashes.stream()
                .findFirst()
                .orElseThrow(() -> new BitcoinBlockException("Failed to process BitcoinBlock. Hashes are empty."));

        log.info("Batch {} started", firstHashInBlocks);

        hashes.forEach(hash -> {
            try {
                BlockInfo block = client.getBlockInfo(Sha256Hash.wrap(hash));
                listOfBlocks.add(mapper.toEntity(block));
            } catch (IOException e) {
                throw new BitcoinBlockException("Failed to process BitcoinBlock hashes " + firstHashInBlocks);
            }
        });

        transactionalService.saveBitcoinBlocksInfosAndUpdateStatuses(listOfBlocks, hashes);

        log.info("Batch {} done", firstHashInBlocks);
    }
}
