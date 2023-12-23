package com.hiperboot.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hiperboot.pckagetest.ParentTable;

@Repository
public interface ParentTableStandardRepository extends JpaRepository<ParentTable, Long> {
}
