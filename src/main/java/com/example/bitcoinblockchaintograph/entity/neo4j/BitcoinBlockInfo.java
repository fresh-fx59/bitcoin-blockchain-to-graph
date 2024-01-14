package com.example.bitcoinblockchaintograph.entity.neo4j;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.time.Instant;

//@Data
@Setter
@Getter
@Builder
@Node("bitcoin_block_info")
public class BitcoinBlockInfo {
    @Id
    @GeneratedValue
    private String id;

    private String hash;
    private int confirmations;
    private int size;
    private int height;
    private int version;
    private String merkleroot;
    private final int nTx;
    //private BlockInfo.Sha256HashList tx;
    private Instant time;
    private long nonce;
    private String bits;
    private BigDecimal difficulty;
    private String chainwork;
    private String previousblockhash;
    private String nextblockhash;

    @Relationship(type = "BLOCK_RELATION", direction = Relationship.Direction.OUTGOING)
    private BitcoinBlockInfo previousBlock;

    @Relationship(type = "BLOCK_RELATION", direction = Relationship.Direction.INCOMING)
    private BitcoinBlockInfo nextBlock;
}
