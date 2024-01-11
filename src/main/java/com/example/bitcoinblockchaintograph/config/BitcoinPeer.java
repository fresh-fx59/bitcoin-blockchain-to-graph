package com.example.bitcoinblockchaintograph.config;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.base.Network;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.params.BitcoinNetworkParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Slf4j
@Configuration
public class BitcoinPeer implements AutoCloseable {

    private PeerGroup peerGroup;

    public Peer getPeer() throws BlockStoreException, ExecutionException, InterruptedException {
        // Connect to testnet and find a peer
        log.info("Connecting to node");
        BitcoinNetworkParams params = TestNet3Params.get();

        final Network network = params.network();
        BlockStore blockStore = new MemoryBlockStore(params.getGenesisBlock());
        BlockChain chain = new BlockChain(network, blockStore);

        peerGroup = new PeerGroup(network, chain);

        peerGroup.start();
        peerGroup.waitForPeers(1).get();

        return peerGroup.getConnectedPeers().getFirst();
    }

    @Override
    public void close() {
        peerGroup.stopAsync();
    }
}
