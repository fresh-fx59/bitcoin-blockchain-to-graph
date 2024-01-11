package com.example.bitcoinblockchaintograph.repository.postgres;

import com.example.bitcoinblockchaintograph.entity.postgres.BitcoinBlockInitialData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface BitcoinBlockInitialDataRepository extends JpaRepository<BitcoinBlockInitialData, Long> {
    @Query("select max(b.blockNumber) from BitcoinBlockInitialData b")
    Integer findMaxBlockNumber();

    @Query("select b.blockNumber from BitcoinBlockInitialData b")
    Set<Integer> findAllBlocksNumbers();
}
