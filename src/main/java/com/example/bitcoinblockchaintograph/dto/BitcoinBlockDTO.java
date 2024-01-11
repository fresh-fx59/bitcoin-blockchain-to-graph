package com.example.bitcoinblockchaintograph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;


/**
 * Block data example
 *
 *    hash: 000000000000001819f3737d6742e1e7e44b7f8ae5f4d1785d6c8ca210504d61
 *    version: 547356672 (BIP34, BIP66, BIP65)
 *    previous block: 000000000000000e5bae3f07566463c317688717c2db7c4b054c97281b660572
 *    time: 1703170874 (2023-12-21T15:01:14Z)
 *    difficulty target (nBits): 422109167
 *    nonce: 157527665
 *    merkle root: b85c0023d03cad0968fab962cfe27e80972e183f8380abb299729c5893ed4f62
 *    witness root: 88d89846a6e27cf2074565e45989bf5a1ee5375f64df2274105f144f3ccfa2a3
 */
@Data
@AllArgsConstructor
@Builder
public class BitcoinBlockDTO {
    private Long id;
    private String blockHash;
    private Long blockVersion;
    private String previousBlockHash;
    private Date timestamp;
    private Long difficulty;
    private Long nonce;
    private String merkleRoot;
    private String witnessRoot;
    private Integer blockNumber;
    private BitcoinBlockDTO previousBlock;
}
