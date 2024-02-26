/*
 * Copyright 2002-2024 by Sannon Gualda de Aragão.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiperboot.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import lombok.extern.log4j.Log4j2;

//@Profile("postgresql")
@Log4j2
@TestConfiguration
public class PostgresContainerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresContainerConfig.class);

    private DataSource dataSource;
    private static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        startPostgres();
    }

    public static void startPostgres() {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
                .withDatabaseName("hiperboot")
                .withUsername("postgres")
                .withPassword("postgres");

        postgreSQLContainer.start();
        postgreSQLContainer.followOutput(new Slf4jLogConsumer(LOGGER));

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @Bean
    @Primary
    public synchronized DataSource dataSource() {
        return PostgresContainerConfig.createDataSource();
    }

    private static DataSource createDataSource() {
        BasicDataSource newDataSource = new BasicDataSource();
        newDataSource.setDriverClassName(postgreSQLContainer.getDriverClassName());
        newDataSource.setUrl(postgreSQLContainer.getJdbcUrl());
        newDataSource.setUsername(postgreSQLContainer.getUsername());
        newDataSource.setPassword(postgreSQLContainer.getPassword());
        newDataSource.setValidationQuery("CREATE EXTENSION citext;");

        return newDataSource;
    }
}
