package com.hiperboot.db.entity;

import java.util.List;

import com.hiperboot.pckagetest.ParentTable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "child_table")
public class ChildTable {

    @Id
    @Column(name = "name")
    private String name;

    private Integer number;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ParentTable parent;

    @OneToMany
    @JoinColumn(name = "child_table_name", updatable = false, insertable = false)
    private List<GranChildTable> granChild;

}
