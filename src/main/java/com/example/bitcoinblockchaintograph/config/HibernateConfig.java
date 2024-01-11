package com.example.bitcoinblockchaintograph.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class HibernateConfig {

    private final JpaCustomProperties prop;

    public Map<String, Object> getHibernateProperties() {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.show_sql", prop.isShowSql());
        properties.put("hibernate.use_sql_comments", prop.isUseSqlComments());
        properties.put("hibernate.format_sql", prop.isFormatSql());
        properties.put("hibernate.generate_statistics", prop.isGenerateStatistics());
        properties.put("hibernate.order_inserts", prop.isOrderInserts());
        properties.put("hibernate.order_updates", prop.isOrderUpdates());
        properties.put("hibernate.jdbc.batch_size", prop.getJdbc().getBatchSize());
        properties.put("hibernate.jdbc.batch_versioned_data", prop.getJdbc().isBatchVersionedData());
        properties.put("hibernate.id.new_generator_mappings", prop.getId().isNewGeneratorMappings());
        properties.put("hibernate.dialect", prop.getDialect());
        properties.put("hibernate.hbm2ddl.auto", prop.getDdlAuto());
        properties.put("hibernate.physical_naming_strategy", prop.getPhysicalNamingStrategy());
        properties.put("hibernate.implicit_naming_strategy", prop.getImplicitNamingStrategy());

        return properties;

    }

}
