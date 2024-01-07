package com.example.bitcoinblockchainml.repository;

import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BitcoinBlockRepository extends Neo4jRepository<BitcoinBlock, Long> {
    Optional<BitcoinBlock> findOneByBlockHash(String blockHash);

//    @Query("MATCH (b:BitcoinBlock) WHERE b.blockHash = $title RETURN b")
    @Query("MATCH (b:bitcoin_block) RETURN max(b.blockNumber)")
    Integer findMaxBlockNumber();
}
