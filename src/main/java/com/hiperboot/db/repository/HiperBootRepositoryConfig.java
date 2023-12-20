package com.hiperboot.db.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = HiperBootRepositoryFactoryBean.class)
public class HiperBootRepositoryConfig {
    // Configuration if needed
}
