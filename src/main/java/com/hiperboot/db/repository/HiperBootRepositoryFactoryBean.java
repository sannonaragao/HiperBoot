package com.hiperboot.db.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryFactoryBean<R extends HiperBootRepository<T, ID>, T, ID extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, ID> {

    public HiperBootRepositoryFactoryBean(Class<R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new HiperBootRepositoryFactory(entityManager);
    }
}
