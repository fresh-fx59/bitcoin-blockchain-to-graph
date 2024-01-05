package com.example.bitcoinblockchainml.service;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import org.consensusj.bitcoin.json.pojo.BlockChainInfo;

public interface BitcoinBlockService {
    BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception;
    BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception;
    BlockChainInfo getBlockChainInfo() throws Exception;
}
