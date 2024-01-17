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
package com.hiperboot.db.entity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hiperboot.db.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "parent_table")
public class ParentTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "some_table_id")
    private SomeTable someTable;

    @OneToMany(mappedBy = "parent")
    private List<ChildTable> children = new ArrayList<>();

    @Column(name = "col_string")
    private String colString;

    @Column(name = "col_long")
    private Long colLong;

    @Column(name = "col_integer")
    private Integer colInteger;

    @Column(name = "col_short")
    private Short colShort;

    @Column(name = "col_byte")
    private Byte colByte;
    private Double colDouble;
    private Float colFloat;
    private Boolean colBoolean;
    private Character colChar;
    @Temporal(TemporalType.DATE)
    private Date colDate;

    @Temporal(TemporalType.TIME)
    private Date colTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date colTimestamp;
    private LocalDate colLocalDate;
    private LocalDateTime colLocalDateTime;
    private BigDecimal colBigDecimal;
    private BigInteger colBigInteger;

    @Column(name = "col_uuid", columnDefinition = "CHAR(36)")
    private String colUUID;

    @Enumerated(EnumType.STRING)
    @Column(name = "col_status_enum")
    private StatusEnum colStatusEnum;
}