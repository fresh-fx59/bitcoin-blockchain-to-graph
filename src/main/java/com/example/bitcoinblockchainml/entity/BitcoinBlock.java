package com.example.bitcoinblockchainml.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.Date;

@Builder
@Node("BitcoinBlock")
public class BitcoinBlock {
    @Id
    private String id;
    private String blockHash;
    private Long blockVersion;

    private String previousBlockHash;
    private Date timestamp;
    private Long difficulty;
    private Long nonce;
    private String merkleRoot;
    private String witnessRoot;
}
