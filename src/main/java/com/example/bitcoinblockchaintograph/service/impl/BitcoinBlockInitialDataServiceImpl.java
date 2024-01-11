package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.config.PoolConfig;
import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import com.example.bitcoinblockchaintograph.exception.BitcoinBlockInitialDataException;
import com.example.bitcoinblockchaintograph.handler.GetDataFromJsonRpcMultiThread;
import com.example.bitcoinblockchaintograph.handler.ProcessDataFromJsonRpcMultiThread;
import com.example.bitcoinblockchaintograph.repository.postgres.BitcoinBlockInitialDataRepository;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockInitialDataService;
import com.example.bitcoinblockchaintograph.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinBlockInitialDataServiceImpl implements BitcoinBlockInitialDataService {

    @Value("${batch.size}")
    private int batchSize;
    @Value("${batch.parallelism}")
    private int parallelism;
    private final BitcoinBlockInitialDataRepository repository;
    private final BitcoinClient client;
    private final TransactionTemplate transactionTemplate;
    private final TransactionalService transactionalService;

    @Override
    public void loadBitcoinBlockInitialData() throws IOException {
        Set<Integer> loadedBlocksNumbers = repository.findAllBlocksNumbers();
        int maxBlockNumberFromBlockChain = client.getBlockChainInfo().getBlocks();

        List<Integer> allBlockNumbersFromBlockChain = new ArrayList<>(IntStream
                .rangeClosed(0, maxBlockNumberFromBlockChain)
                .boxed()
                .toList());
        
        allBlockNumbersFromBlockChain.removeAll(loadedBlocksNumbers);

        List<List<Integer>> batchedNumbers = new ArrayList<>();
        for (int i = 0; i < allBlockNumbersFromBlockChain.size(); i += batchSize) {
            batchedNumbers.add(
                    allBlockNumbersFromBlockChain
                            .subList(i, Math.min(i + batchSize, allBlockNumbersFromBlockChain.size())));
        }

        try (ExecutorService customThreadPool = Executors.newFixedThreadPool(parallelism)) {
            customThreadPool.submit(() -> batchedNumbers.parallelStream().forEach(this::processBlocksNumbers));
        }

    }

    private void processBlocksNumbers(List<Integer> blocksNumbers) {
        List<BitcoinBlockInitialData> listOfBlocks = new ArrayList<>();

        blocksNumbers.forEach(blockNumber -> {
            try {
                listOfBlocks.add(getBitcoinBlockInitialDataFromBlockchain(blockNumber));
            } catch (IOException e) {
                throw new BitcoinBlockInitialDataException("Failed to process BitcoinBlockInitialData");
            }
        });

        transactionalService.saveBitcoinBlocksInitialData(listOfBlocks);

        log.info("Batch {} done", blocksNumbers.getFirst());
    }
    
    private BitcoinBlockInitialData getBitcoinBlockInitialDataFromBlockchain(Integer blockNumber) throws IOException {
        String hash = String.valueOf(client.getBlockHash(blockNumber));
        return new BitcoinBlockInitialData(hash, blockNumber);
    }

    public void loadBitcoinBlockInitialDataMultithread() throws IOException {
        Integer blocksCount = client.getBlockChainInfo().getBlocks();
        Integer maxBlockNumberFromDb = repository.findMaxBlockNumber();
        int startingBlock = maxBlockNumberFromDb != null ? maxBlockNumberFromDb + 1 : 0;

        if (maxBlockNumberFromDb != null) {
            if (blocksCount.compareTo(maxBlockNumberFromDb) == 0) {
                log.info("All blocks are loaded");
                return;
            } else if (blocksCount.compareTo(maxBlockNumberFromDb) < 0) {
                log.info("Something went wrong: blocksCount = " + blocksCount
                        + " is less than maxBlockNumberFromDb = " + maxBlockNumberFromDb);
                return;
            }
        }

        processBlocksMultithread(startingBlock, blocksCount);
    }

    /**
     * Process blocks multi thread
     * @param startingBlock starting block
     * @param blocksCount all blocks count
     */
    private void processBlocksMultithread(int startingBlock,  int blocksCount) {
        int batchCount = (((blocksCount - startingBlock) / batchSize) + 3) / 4;

        try (ExecutorService executor = Executors.newFixedThreadPool(parallelism)) {
            for (int currentBatch = 1; currentBatch <= batchCount; currentBatch++) {
                for (int i = 1; i <= parallelism; i++) {
                    int currentBatchF = currentBatch - 1;
                    executor.submit(() -> {
                        try {
                            int startingBlockInBatch = startingBlock + (currentBatchF * batchSize);
                            int endBlockInBatch = startingBlock + ((currentBatchF + 1) * batchSize) - 1;
                            processBatch(startingBlockInBatch, endBlockInBatch);
                            log.info("Batch {} ends", currentBatchF + 1);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        }
    }

    private void processBatch(Set<Integer> blockNumbers) throws IOException {
        List<BitcoinBlockInitialData> blocksData = new ArrayList<>();

        blockNumbers.forEach(blockNumber -> {
            try {
                String hash = String.valueOf(client.getBlockHash(blockNumber));
                blocksData.add(new BitcoinBlockInitialData(hash, blockNumber));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        transactionalService.saveBitcoinBlocksInitialData(blocksData);
    }

    private void processBatch(int startingBlock, int endBlock) throws IOException {
        List<BitcoinBlockInitialData> blocksData = new ArrayList<>();

        for (int i = startingBlock; i <= endBlock; i++) {
                        blocksData.add(getBitcoinBlockInitialDataFromBlockchain(i));
        }

        transactionalService.saveBitcoinBlocksInitialData(blocksData);
    }

    private void processInBatches(int startingBlock, int blocksCount, int batchSize) throws IOException {
        List<BitcoinBlockInitialData> blocksData = new ArrayList<>();
        int batchSizeCounter = 0;

        for (int i = startingBlock; i <= blocksCount; i++) {
                        blocksData.add(getBitcoinBlockInitialDataFromBlockchain(i));
            batchSizeCounter++;
            if (batchSizeCounter >= batchSize || i == blocksCount) {
                transactionalService.saveBitcoinBlocksInitialData(blocksData);
                batchSizeCounter = 0;
            }
        }
    }

    public void loadBitcoinBlockInitialDataForkJoinPool() throws IOException {
        Integer blocksCount = client.getBlockChainInfo().getBlocks();
        Integer maxBlockNumberFromDb = repository.findMaxBlockNumber();
        int startingBlock = maxBlockNumberFromDb != null ? maxBlockNumberFromDb + 1 : 0;

        if (maxBlockNumberFromDb != null) {
            if (blocksCount.compareTo(maxBlockNumberFromDb) == 0) {
                log.info("All blocks are loaded");
                return;
            } else if (blocksCount.compareTo(maxBlockNumberFromDb) < 0) {
                log.info("Something went wrong: blocksCount = " + blocksCount
                        + " is less than maxBlockNumberFromDb = " + maxBlockNumberFromDb);
                return;
            }
        }

        PoolConfig.forkJoinPool.invoke(
                new ProcessDataFromJsonRpcMultiThread(startingBlock, blocksCount, 100, client, repository, transactionTemplate));
    }

    private void loadBitcoinBlockInitialData2() throws IOException {
        Integer blocksCount = client.getBlockChainInfo().getBlocks();
        Integer maxBlockNumberFromDb = repository.findMaxBlockNumber();
        int startingBlock = maxBlockNumberFromDb != null ? maxBlockNumberFromDb + 1 : 0;

        if (maxBlockNumberFromDb != null) {
            if (blocksCount.compareTo(maxBlockNumberFromDb) == 0) {
                log.info("All blocks are loaded");
                return;
            } else if (blocksCount.compareTo(maxBlockNumberFromDb) < 0) {
                log.info("Something went wrong: blocksCount = " + blocksCount
                        + " is less than maxBlockNumberFromDb = " + maxBlockNumberFromDb);
                return;
            }
        }

        //if (blocksCount - startingBlock < )

        List<BitcoinBlockInitialData> blocksData = PoolConfig.forkJoinPool.invoke(
                new GetDataFromJsonRpcMultiThread(startingBlock, blocksCount, 10_000, client));

        repository.saveAll(blocksData);
    }


    private void loadBitcoinBlockInitialData0() throws IOException {
        Integer blocksCount = client.getBlockChainInfo().getBlocks();
        Integer maxBlockNumberFromDb = repository.findMaxBlockNumber();
        int startingBlock = maxBlockNumberFromDb != null ? maxBlockNumberFromDb + 1 : 0;

        if (maxBlockNumberFromDb != null) {
            if (blocksCount.compareTo(maxBlockNumberFromDb) == 0) {
                log.info("All blocks are loaded");
                return;
            } else if (blocksCount.compareTo(maxBlockNumberFromDb) < 0) {
                log.info("Something went wrong: blocksCount = " + blocksCount
                        + " is less than maxBlockNumberFromDb = " + maxBlockNumberFromDb);
                return;
            }
        }

        List<BitcoinBlockInitialData> blocksData = new ArrayList<>();

        for (int i = startingBlock; i <= blocksCount; i++) {
                        blocksData.add(getBitcoinBlockInitialDataFromBlockchain(i));
        }

        repository.saveAll(blocksData);
    }
}
