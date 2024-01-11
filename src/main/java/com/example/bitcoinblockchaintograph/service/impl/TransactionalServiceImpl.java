package com.example.bitcoinblockchaintograph.service.impl;

import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveBitcoinBlocksInitialData(Collection<BitcoinBlockInitialData> blocks) {
        bitcoinBlockInitialDataRepository.saveAll(blocks);
    }
}
