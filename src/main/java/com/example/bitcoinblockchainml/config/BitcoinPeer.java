package com.example.bitcoinblockchainml.config;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class BitcoinPeer implements AutoCloseable {

    private PeerGroup peerGroup;

    public Peer getPeer() throws BlockStoreException, ExecutionException, InterruptedException {
        // Connect to testnet and find a peer
        log.info("Connecting to node");
        final NetworkParameters params = TestNet3Params.get();
        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, blockStore);

        peerGroup = new PeerGroup(params, chain);

        peerGroup.start();
        peerGroup.waitForPeers(1).get();

        return peerGroup.getConnectedPeers().getFirst();
    }

    @Override
    public void close() throws Exception {
        peerGroup.stopAsync();
    }
}
