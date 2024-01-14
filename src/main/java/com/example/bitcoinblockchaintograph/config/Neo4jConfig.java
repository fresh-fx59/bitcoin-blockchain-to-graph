package com.example.bitcoinblockchaintograph.config;

import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableNeo4jRepositories(basePackages = "com.example.bitcoinblockchaintograph.repository.neo4j",
        transactionManagerRef = "neo4jTransactionManager")
@EnableTransactionManagement
@Configuration
public class Neo4jConfig extends Neo4jDataAutoConfiguration {

    @Value("${spring.neo4j.uri}")
    private String url;
    @Value("${spring.neo4j.authentication.username}")
    private String username;
    @Value("${spring.neo4j.authentication.password}")
    private String password;

    @Bean
    public org.neo4j.cypherdsl.core.renderer.Configuration cypherDslConfiguration() {
        return org.neo4j.cypherdsl.core.renderer.Configuration.newConfig()
                .withDialect(Dialect.NEO4J_5).build();
    }

    @Bean
    public Driver getConfiguration() {
        return GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }

    @Bean("neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager() {
        return new Neo4jTransactionManager(getConfiguration());
    }
}
