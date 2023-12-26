package com.hiperboot.db.repository;

import org.springframework.stereotype.Repository;

import com.hiperboot.db.entity.book.Author;

@Repository
public interface AuthorHiperBootRepository extends HiperBootRepository<Author, Long> {
}