package com.example.bitcoinblockchaintograph.service;

import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlock;
import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlockInfo;
import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;

import java.util.Collection;

public interface TransactionalService {
    void saveBitcoinBlocksInitialData(Collection<BitcoinBlockInitialData> blocks);
    void saveBitcoinBlocksAndUpdateStatuses(Collection<BitcoinBlock> blocks, Collection<String> hashes);
    void saveBitcoinBlocksInfosAndUpdateStatuses(Collection<BitcoinBlockInfo> blocks, Collection<String> hashes);
}
