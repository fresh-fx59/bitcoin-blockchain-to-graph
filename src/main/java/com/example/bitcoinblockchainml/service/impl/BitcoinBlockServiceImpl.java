package com.example.bitcoinblockchainml.service.impl;

import com.example.bitcoinblockchainml.config.BitcoinPeer;
import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import com.example.bitcoinblockchainml.mapper.BitcoinBlockMapper;
import com.example.bitcoinblockchainml.repository.BitcoinBlockRepository;
import com.example.bitcoinblockchainml.service.BitcoinBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.Block;
import org.consensusj.bitcoin.json.pojo.BlockChainInfo;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinBlockServiceImpl implements BitcoinBlockService {

    private final BitcoinBlockMapper mapper;
    private final BitcoinBlockRepository repository;
    private final BitcoinClient client;

    @Override
    public BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception {
        // Retrieve a block through a peer
        Sha256Hash blockHash = Sha256Hash.wrap(blockHashParam);
        try (BitcoinPeer bitcoinPeer = new BitcoinPeer()) {
            Future<Block> future = bitcoinPeer.getPeer().getBlock(blockHash);
            log.info("Waiting for node to send us the requested block: " + blockHash);
            Block block = future.get();
            return mapper.toDto(block);
        }
    }

    @Override
    public BlockChainInfo getBlockChainInfo() throws Exception {
        // Retrieve a block through a peer
        return client.getBlockChainInfo();
    }

    @Override
    public BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception {        // Retrieve a block through a peer
        BitcoinBlockDTO blockDto = getBlockFromPeer(blockHashParam);
        BitcoinBlock block = mapper.toEntity(blockDto);

        return mapper.toDto(repository.save(block));
    }

}
