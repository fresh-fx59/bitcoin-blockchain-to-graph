package com.example.bitcoinblockchaintograph.service;

import com.example.bitcoinblockchaintograph.dto.BitcoinBlockDTO;
import org.consensusj.bitcoin.json.pojo.BlockChainInfo;

import java.io.IOException;

public interface BitcoinBlockService {
    void loadNotLoadedBlocks() throws IOException;
    BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception;
    BitcoinBlockDTO getBlockFromDb(String blockHashParam) throws Exception;
    BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception;
    BitcoinBlockDTO saveBlockFromClientToDb(Integer id) throws Exception;
    BlockChainInfo getBlockChainInfo() throws Exception;
    BitcoinBlockDTO enrichBlock(String hash) throws Exception;
    String loadNodes() throws IOException;
}
