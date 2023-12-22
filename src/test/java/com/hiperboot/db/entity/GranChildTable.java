package com.hiperboot.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gran_child_table")
public class GranChildTable {

    @Id
    @Column(name = "gran_name")
    private String granName;

    private String something;

    @ManyToOne
    @JoinColumn(name = "child_table_name", referencedColumnName = "name")
    private ChildTable childTable;


}
