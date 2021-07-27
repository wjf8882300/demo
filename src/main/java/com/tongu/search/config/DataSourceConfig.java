package com.tongu.search.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.tongu.search.service.BatchService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "footballDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.football")
    public DataSource footballDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "basketballDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.basketball")
    public DataSource basketballDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "localeDataSource")
    @ConfigurationProperties(prefix="spring.datasource.druid.locale")
    public DataSource localeDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "footballJdbcTemplate")
    public NamedParameterJdbcTemplate footballJdbcTemplate(
            @Qualifier("footballDataSource") DataSource sourceDataSource){
        return new NamedParameterJdbcTemplate(sourceDataSource);
    }

    @Bean(name = "basketballJdbcTemplate")
    public NamedParameterJdbcTemplate basketballJdbcTemplate(
            @Qualifier("basketballDataSource") DataSource sourceDataSource){
        return new NamedParameterJdbcTemplate(sourceDataSource);
    }

    @Bean(name = "localeJdbcTemplate")
    public NamedParameterJdbcTemplate localeJdbcTemplate(
            @Qualifier("localeDataSource") DataSource localeDataSource){
        return new NamedParameterJdbcTemplate(localeDataSource);
    }
}
