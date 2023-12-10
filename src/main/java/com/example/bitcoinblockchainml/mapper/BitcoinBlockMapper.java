package com.example.bitcoinblockchainml.mapper;

import com.example.bitcoinblockchainml.dto.BitcoinBlockDTO;
import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import org.bitcoinj.core.Block;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN)
public abstract class BitcoinBlockMapper implements EntityMapper<BitcoinBlockDTO, BitcoinBlock>{

    public BitcoinBlockDTO toDto(Block block) {
        return BitcoinBlockDTO.builder()
                .blockHash(block.getHashAsString())
                .blockVersion(block.getVersion())
                .previousBlockHash(block.getPrevBlockHash().toString())
                .timestamp(block.getTime())
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
                .timestamp(block.getTime())
                .difficulty(block.getDifficultyTarget())
                .nonce(block.getNonce())
                .merkleRoot(block.getMerkleRoot().toString())
                .witnessRoot(block.getWitnessRoot().toString())
                .build();
    }

}
