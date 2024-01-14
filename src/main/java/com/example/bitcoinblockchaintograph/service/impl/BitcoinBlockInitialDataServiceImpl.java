package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus;
import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import com.example.bitcoinblockchaintograph.exception.BitcoinBlockInitialDataException;
import com.example.bitcoinblockchaintograph.repository.postgres.BitcoinBlockInitialDataRepository;
import com.example.bitcoinblockchaintograph.service.BitcoinBlockInitialDataService;
import com.example.bitcoinblockchaintograph.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus.INITIAL_DATA_SAVED;
import static com.example.bitcoinblockchaintograph.util.BatchTool.getBatchedLists;

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
    private final TransactionalService transactionalService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateBlocksStatus(Collection<String> hashes, BlockStatus statusToSet) {
        repository.updateStatus(hashes, statusToSet);
    }

    @Override
    public Set<String> getNotLoadedHashes() {
        return repository.findAllHashesInStatus(INITIAL_DATA_SAVED);
    }

    @Override
    public void loadBitcoinBlockInitialData() throws IOException {
        List<Integer> loadedBlocksNumbers = repository.findAllBlocksNumbers();
        int maxBlockNumberFromBlockChain = client.getBlockChainInfo().getBlocks();

        List<Integer> allBlockNumbersFromBlockChain = new ArrayList<>(IntStream
                .rangeClosed(0, maxBlockNumberFromBlockChain)
                .boxed()
                .toList());
        
        allBlockNumbersFromBlockChain.removeAll(loadedBlocksNumbers);

        List<List<Integer>> batchedNumbers = getBatchedLists(allBlockNumbersFromBlockChain, batchSize);

        try (ExecutorService customThreadPool = Executors.newFixedThreadPool(parallelism)) {
            customThreadPool.submit(() -> batchedNumbers.parallelStream().forEach(this::processBlocksNumbers));
        }
    }

    /**
     * Get BitcoinlockInitialData and save it in new transaction.
     * @param blocksNumbers to retrieve from blockchain
     */
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
}
