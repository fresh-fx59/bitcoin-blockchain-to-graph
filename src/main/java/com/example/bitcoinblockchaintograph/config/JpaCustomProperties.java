package com.example.bitcoinblockchaintograph.config;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties(prefix = "spring.jpa.properties.hibernate")
public class JpaCustomProperties {

    private boolean showSql;
    private boolean useSqlComments;
    private boolean formatSql;
    private boolean generateStatistics;
    private boolean orderInserts;
    private boolean orderUpdates;
    private String physicalNamingStrategy;
    private String implicitNamingStrategy;
    @NotNull
    private JdbcCustomProperties jdbc;
    @NotNull
    private IdCustomProperties id;
    @NotNull
    private String dialect;
    @NotNull
    private String ddlAuto;

    @Data
    public static class JdbcCustomProperties {
        private int batchSize;
        private boolean batchVersionedData;
    }

    @Data
    public static class IdCustomProperties {
        private boolean newGeneratorMappings;
    }
}
