package com.example.bitcoinblockchainml.repository;

import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitcoinBlockRepository extends Neo4jRepository<BitcoinBlock, Long> {
    //@Query("MATCH (b:BitcoinBlock) WHERE b.blockHash = $title RETURN b")
    BitcoinBlock findOneByBlockHash(String blockHash);
}
