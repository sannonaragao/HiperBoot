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

    @Column(name = "col_double")
    private Double colDouble;

    @Column(name = "col_float")
    private Float colFloat;

    @Column(name = "col_boolean")
    private Boolean colBoolean;

    @Column(name = "col_char")
    private Character colChar;

    @Column(name = "col_date")
    @Temporal(TemporalType.DATE)
    private Date colDate;

    @Column(name = "col_time")
    @Temporal(TemporalType.TIME)
    private Date colTime;

    @Column(name = "col_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date colTimestamp;

//    @Column(name = "col_local_date")
    private LocalDate colLocalDate;

//    @Column(name = "col_local_date_time")
    private LocalDateTime colLocalDateTime;

//    @Column(name = "col_big_decimal")
    private BigDecimal colBigDecimal;

//    @Column(name = "col_big_integer")
    private BigInteger colBigInteger;

    @Column(name = "col_uuid", columnDefinition = "CHAR(36)")
    private String colUUID;

    @Enumerated(EnumType.STRING)
    @Column(name = "col_status_enum")
    private StatusEnum colStatusEnum;
}