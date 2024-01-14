package com.example.bitcoinblockchaintograph.mapper;

import com.example.bitcoinblockchaintograph.entity.neo4j.BitcoinBlockInfo;
import org.consensusj.bitcoin.json.pojo.BlockInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.WARN, componentModel = "spring")
public interface BitcoinBlockInfoMapper {

    @Mapping(target = "hash", expression = "java(dto.getHash().toString())")
    @Mapping(target = "merkleroot", expression = "java(dto.getMerkleroot().toString())")
    @Mapping(target = "previousblockhash", expression = "java(dto.getPreviousblockhash().toString())")
    @Mapping(target = "nextblockhash", expression = "java(dto.getNextblockhash().toString())")
    BitcoinBlockInfo toEntity(BlockInfo dto);

    List<BitcoinBlockInfo> toEntity(List<BlockInfo> dtoList);
}
