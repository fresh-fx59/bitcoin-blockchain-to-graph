package com.example.bitcoinblockchaintograph.repository.neo4j;

import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlock;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface BitcoinBlockRepository extends Neo4jRepository<BitcoinBlock, String> {
    Optional<BitcoinBlock> findOneByBlockHash(String blockHash);

//    @Query("MATCH (b:BitcoinBlock) WHERE b.blockHash = $title RETURN b")
    @Query("MATCH (b:bitcoin_block) RETURN max(b.blockNumber)")
    Integer findMaxBlockNumber();
    @Query("MATCH (b:bitcoin_block) RETURN b.blockHash")
    Set<String> findAllBlocksHashes();
}
