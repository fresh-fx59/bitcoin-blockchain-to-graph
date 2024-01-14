package com.example.bitcoinblockchaintograph.repository.postgres;

import com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus;
import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;


@Repository
public interface BitcoinBlockInitialDataRepository extends JpaRepository<BitcoinBlockInitialData, Long> {
    @Query("select max(b.blockNumber) from BitcoinBlockInitialData b")
    Integer findMaxBlockNumber();

    @Query("select b.blockNumber from BitcoinBlockInitialData b")
    List<Integer> findAllBlocksNumbers();

    @Query("select b.hash from BitcoinBlockInitialData b where b.status = :status")
    Set<String> findAllHashesInStatus(BlockStatus status);

    @Modifying
    @Query("UPDATE BitcoinBlockInitialData b SET b.status = :status WHERE b.hash in :hashes")
    void updateStatus(Collection<String> hashes, BlockStatus status);
}
