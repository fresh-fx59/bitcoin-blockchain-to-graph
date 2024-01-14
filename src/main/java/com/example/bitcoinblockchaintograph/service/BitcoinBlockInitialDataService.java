package com.example.bitcoinblockchaintograph.service;

import com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface BitcoinBlockInitialDataService {
    void loadBitcoinBlockInitialData() throws IOException;

    /**
     * Get hashses of blocks in status INITIAL_DATA_SAVED
     * @return set of hashes
     */
    Set<String> getNotLoadedHashes();

    void updateBlocksStatus(Collection<String> hashes, BlockStatus statusToSet);
}
