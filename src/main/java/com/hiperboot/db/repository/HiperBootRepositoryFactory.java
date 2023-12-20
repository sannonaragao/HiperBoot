package com.hiperboot.db.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import jakarta.persistence.EntityManager;

public class HiperBootRepositoryFactory extends JpaRepositoryFactory {

    public HiperBootRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
            EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return new HiperBootRepositoryImpl<>(entityInformation, entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        // The custom implementation
        return HiperBootRepositoryImpl.class;
    }
}
