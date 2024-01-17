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
package com.hiperboot.db.filter;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
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
    private Class<?> originalClass;
    private LogicalOperator logicalOperator;
    private LogicalOperator wrappedLogicalOperator;
    private ArrayList<ControlFlag> controlFlag;
}
