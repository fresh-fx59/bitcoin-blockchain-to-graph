package com.example.bitcoinblockchaintograph.config;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.params.TestNet3Params;
import org.consensusj.bitcoin.jsonrpc.BitcoinClient;
import org.consensusj.bitcoin.jsonrpc.RpcConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
public class BitcoinConfig {

    @Bean
    public BitcoinClient getClient() throws URISyntaxException, IOException {
        URI uri = new URI("http://localhost:18332/");
        RpcConfig config = new RpcConfig(TestNet3Params.get().network(), uri, "user", "password");
        BitcoinClient client = new BitcoinClient(config);
        log.info("Blocks count " + client.getBlockChainInfo().getBlocks());
        return client;
    }
}
