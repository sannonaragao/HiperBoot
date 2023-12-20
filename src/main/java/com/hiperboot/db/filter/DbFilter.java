package com.hiperboot.db.filter;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DbFilter {
    private String field;
    private QueryOperator operator;
    private Object value;
    private ArrayList<?> values;
    private boolean entity;
    private Class<?> type;
    private LogicalOperator logicalOperator;
    private LogicalOperator wrappedLogicalOperator;
    private ArrayList<ControlFlag> controlFlag;
}
