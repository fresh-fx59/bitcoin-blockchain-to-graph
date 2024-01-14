package com.example.bitcoinblockchaintograph.repository.neo4j;

import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlockInfo;
import org.bitcoinj.base.Sha256Hash;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface BitcoinBlockInfoRepository extends Neo4jRepository<BitcoinBlockInfo, String> {
    Optional<BitcoinBlockInfo> findOneByHash(Sha256Hash hash);
    List<BitcoinBlockInfo> findAllByHashIn(Collection<Sha256Hash> hashes);
    @Query("MATCH (b:bitcoin_block_info) RETURN b.hash")
    List<Sha256Hash> findAllBlocksHashes();
}
