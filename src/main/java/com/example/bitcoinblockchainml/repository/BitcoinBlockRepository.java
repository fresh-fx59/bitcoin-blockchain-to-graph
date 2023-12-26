package com.example.bitcoinblockchainml.repository;

import com.example.bitcoinblockchainml.entity.BitcoinBlock;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BitcoinBlockRepository extends Neo4jRepository<BitcoinBlock, Long> {
    /**
     * This repository is indirectly used in the {@code movies.spring.data.neo4j.api.MovieController} via a dedicated movie service.
     * It is not a public interface to indicate that access is either through the rest resources or through the service.
     *
     * @author Michael Hunger
     * @author Mark Angrish
     * @author Michael J. Simons
     */

    @Query("MATCH (movie:Movie) WHERE movie.title CONTAINS $title RETURN bitcoin")
    List<BitcoinBlock> findSearchResults(@Param("title") String title);
}
