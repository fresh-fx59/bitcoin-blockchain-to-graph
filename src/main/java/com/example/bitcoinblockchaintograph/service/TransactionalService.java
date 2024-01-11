package com.example.bitcoinblockchaintograph.service;

import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;

import java.util.Collection;

public interface TransactionalService {
    void saveBitcoinBlocksInitialData(Collection<BitcoinBlockInitialData> blocks);
}
