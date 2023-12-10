package com.example.bitcoinblockchainml.service;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.repository.BitcoinBlockRepository;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

public interface BitcoinBlockService {
    BitcoinBlockDTO downloadBlock(String blockHashParam) throws Exception;
}
