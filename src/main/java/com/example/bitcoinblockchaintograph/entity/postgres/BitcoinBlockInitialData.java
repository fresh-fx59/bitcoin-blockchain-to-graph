package com.example.bitcoinblockchaintograph.entity.postgres;

import com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

import static com.example.bitcoinblockchaintograph.entity.enumeration.BlockStatus.INITIAL_DATA_SAVED;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bitcoin_block_initial_data")
public class BitcoinBlockInitialData {
    @Id
    //https://stackoverflow.com/questions/12745751/hibernate-sequencegenerator-and-allocationsize
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BitcoinBlockInitialData_SEQ")
    @SequenceGenerator(name = "BitcoinBlockInitialData_SEQ", sequenceName = "bitcoin_block_initial_data_seq")
    @Column(name = "id")
    private Long id;

    private String hash;
    private Integer blockNumber;
    @Enumerated(EnumType.STRING)
    private BlockStatus status = INITIAL_DATA_SAVED;

    public BitcoinBlockInitialData(String hash, Integer blockNumber) {
        this.hash = hash;
        this.blockNumber = blockNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitcoinBlockInitialData that = (BitcoinBlockInitialData) o;
        return Objects.equals(hash, that.hash) && Objects.equals(blockNumber, that.blockNumber) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, blockNumber, status);
    }
}
