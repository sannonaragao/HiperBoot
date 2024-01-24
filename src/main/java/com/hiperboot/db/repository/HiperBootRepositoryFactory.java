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
package com.hiperboot.db.repository;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryFactory extends JpaRepositoryFactory {

    private final ApplicationContext context;

    public HiperBootRepositoryFactory(EntityManager entityManager, ApplicationContext context) {
        super(entityManager);
        this.context = context;
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        ExtraCriteriaStrategy<?> strategy = findExtraCriteriaStrategyImplementation(information.getDomainType());

        return new HiperBootRepositoryImpl<>(entityInformation, entityManager, strategy);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return HiperBootRepositoryImpl.class;
    }

    private <T> ExtraCriteriaStrategy<T> findExtraCriteriaStrategyImplementation(Class<T> domainType) {
        Map<String, ExtraCriteriaStrategy> beans = context.getBeansOfType(ExtraCriteriaStrategy.class);
        for (ExtraCriteriaStrategy<T> strategy : beans.values()) {

            Class<?>[] type = GenericTypeResolver.resolveTypeArguments(strategy.getClass(), ExtraCriteriaStrategy.class);

            if (isNull(type)) {
                throw new NullPointerException("ExtraCriteriaStrategy is null");
            }
            if (type.length < 1) {
                throw new NullPointerException("ExtraCriteriaStrategy is empty");
            }
            if (type[0].equals(domainType)) {
                return strategy;
            }
        }
        return null;
    }
}
