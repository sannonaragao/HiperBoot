package com.hiperboot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "some_table")
public class SomeTable {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private Integer value;

}
