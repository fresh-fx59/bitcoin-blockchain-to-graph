package com.example.bitcoinblockchaintograph.entity.neo4j;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.math.BigDecimal;
import java.util.Date;

//@Data
@Setter
@Getter
@Builder
@Node("bitcoin_block")
public class BitcoinBlock {
    @Id
    @GeneratedValue
    private String id;
    private String blockHash;
    private Long blockVersion;

    private String previousBlockHash;
    private Date timestamp;
    private BigDecimal difficulty;
    private Long nonce;
    private String merkleRoot;
    private String witnessRoot;
    private Integer blockNumber;

    @Relationship(type = "BLOCK_RELATION", direction = Relationship.Direction.OUTGOING)
    private BitcoinBlock previousBlock;
}
