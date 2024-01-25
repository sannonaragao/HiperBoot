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

import java.io.Serializable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryFactoryBean<R extends HiperBootRepository<T, P>, T, P extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, P> implements ApplicationContextAware {

    private ApplicationContext context;

    public HiperBootRepositoryFactoryBean(Class<R> repositoryInterface, ApplicationContext context) {
        super(repositoryInterface);
        this.context = context;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new HiperBootRepositoryFactory(entityManager, context);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
