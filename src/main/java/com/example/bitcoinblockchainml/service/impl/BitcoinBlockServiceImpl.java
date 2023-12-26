package com.example.bitcoinblockchainml.service.impl;

import com.example.bitcoinblockchainml.config.BitcoinPeer;
import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import com.example.bitcoinblockchainml.mapper.BitcoinBlockMapper;
import com.example.bitcoinblockchainml.repository.BitcoinBlockRepository;
import com.example.bitcoinblockchainml.service.BitcoinBlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.Sha256Hash;
import org.neo4j.driver.Driver;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@Service
public class BitcoinBlockServiceImpl implements BitcoinBlockService {

//    private final Peer peer;
    //private final BitcoinPeer bitcoinPeer;
    private final BitcoinBlockMapper mapper;
    private final BitcoinBlockRepository repository;

    @Override
    public BitcoinBlockDTO getBlockFromPeer(String blockHashParam) throws Exception {
        // Retrieve a block through a peer
        Sha256Hash blockHash = Sha256Hash.wrap(blockHashParam);
        try (BitcoinPeer bitcoinPeer = new BitcoinPeer()) {
            Future<Block> future = bitcoinPeer.getPeer().getBlock(blockHash);
            log.info("Waiting for node to send us the requested block: " + blockHash);
            Block block = future.get();
            //peerGroup.stopAsync();

            //log.info(block.toString());

            return mapper.toDto(block);
        }
    }

    @Override
    public BitcoinBlockDTO saveBlockFromPeerToDb(String blockHashParam) throws Exception {        // Retrieve a block through a peer
        BitcoinBlockDTO blockDto = getBlockFromPeer(blockHashParam);
        BitcoinBlock block = mapper.toEntity(blockDto);

        return mapper.toDto(repository.save(block));
    }

}
