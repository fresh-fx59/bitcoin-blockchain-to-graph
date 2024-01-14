package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus;
import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlock;
import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlockInfo;
import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import com.example.bitcoinblockchaintograph.repository.neo4j.BitcoinBlockInfoRepository;
import com.example.bitcoinblockchaintograph.repository.neo4j.BitcoinBlockRepository;
import com.example.bitcoinblockchaintograph.repository.postgres.BitcoinBlockInitialDataRepository;
import com.example.bitcoinblockchaintograph.service.TransactionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class TransactionalServiceImpl implements TransactionalService {

    private final BitcoinBlockInitialDataRepository bitcoinBlockInitialDataRepository;
    private final BitcoinBlockRepository bitcoinBlockRepository;
    private final BitcoinBlockInfoRepository bitcoinBlockInfoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveBitcoinBlocksInitialData(Collection<BitcoinBlockInitialData> blocks) {
        bitcoinBlockInitialDataRepository.saveAll(blocks);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveBitcoinBlocksAndUpdateStatuses(Collection<BitcoinBlock> blocks, Collection<String> hashes) {
        bitcoinBlockInitialDataRepository.updateStatus(hashes, BlockStatus.HEADER_SAVED);
        bitcoinBlockRepository.saveAll(blocks);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveBitcoinBlocksInfosAndUpdateStatuses(Collection<BitcoinBlockInfo> blocks, Collection<String> hashes) {
        bitcoinBlockInitialDataRepository.updateStatus(hashes, BlockStatus.HEADER_SAVED);
        bitcoinBlockInfoRepository.saveAll(blocks);
    }
}
