package com.example.bitcoinblockchainml.service;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;

public interface BitcoinBlockService {
    BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception;
    BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception;
}
