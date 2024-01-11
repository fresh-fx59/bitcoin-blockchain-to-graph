package com.example.bitcoinblockchaintograph.config;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    /**
     * Neo4j version-aware database selector.
     *
     * This is only needed for applications running with both Neo4j versions 3 (where multitenancy is not available) and 4.
     *
     * Ideally, one would run instead (where driver is an instance of org.neo4j.driver.Driver):
     * <code>
     *   String neo4jVersion = driver.session().run("RETURN 1").consume().server().version();
     * </code>
     * ... but this requires permissions that the user configured by default does not have.
     *
     * @param database the configured database name
     * @return DatabaseSelection the corresponding database name for Neo4j 4+ or undefined otherwise
     */
//    @Bean
//    DatabaseSelectionProvider databaseSelectionProvider(@Value("${spring.data.neo4j.database}") String database) {
//        return () -> {
//            String neo4jVersion = System.getenv("NEO4J_VERSION");
//            if (neo4jVersion == null || neo4jVersion.startsWith("4")) {
//                return DatabaseSelection.byName(database);
//            }
//            return DatabaseSelection.undecided();
//        };
//    }
    @Bean
    org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return org.neo4j.cypherdsl.core.renderer.Configuration.newConfig()
                .withDialect(Dialect.NEO4J_5).build();
    }
}