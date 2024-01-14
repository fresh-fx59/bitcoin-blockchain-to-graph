package com.example.bitcoinblockchaintograph.mapper;

import com.example.bitcoinblockchaintograph.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlock;
import org.bitcoinj.core.Block;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.Date;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = "spring")
public abstract class BitcoinBlockMapper implements EntityMapper<BitcoinBlockDTO, BitcoinBlock>{

    public BitcoinBlockDTO toDto(Block block) {
        return BitcoinBlockDTO.builder()
                .blockHash(block.getHashAsString())
                .blockVersion(block.getVersion())
                .previousBlockHash(block.getPrevBlockHash().toString())
                .timestamp(Date.from(block.time()))
                .difficulty(block.getDifficultyTarget())
                .nonce(block.getNonce())
                .merkleRoot(block.getMerkleRoot().toString())
                .witnessRoot(block.getWitnessRoot().toString())
                .build();
    }
    public BitcoinBlock toEntity(Block block) {
        return BitcoinBlock.builder()
                .blockHash(block.getHashAsString())
                .blockVersion(block.getVersion())
                .previousBlockHash(block.getPrevBlockHash().toString())
                .timestamp(Date.from(block.time()))
                .difficulty(BigDecimal.valueOf(block.getDifficultyTarget()))
                .nonce(block.getNonce())
                .merkleRoot(block.getMerkleRoot().toString())
                .witnessRoot(block.getWitnessRoot().toString())
                .build();
    }
}
