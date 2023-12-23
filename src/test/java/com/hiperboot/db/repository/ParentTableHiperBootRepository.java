package com.hiperboot.db.repository;

import org.springframework.stereotype.Repository;

import com.hiperboot.pckagetest.ParentTable;

@Repository
public interface ParentTableHiperBootRepository extends HiperBootRepository<ParentTable, Long> {
}