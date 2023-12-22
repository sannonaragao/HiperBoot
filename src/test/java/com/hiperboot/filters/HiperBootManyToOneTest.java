package com.hiperboot.filters;

import static com.hiperboot.util.HBUtil.columnSubEntity;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hiperboot.BaseTestClass;
import com.hiperboot.db.entity.MainTable;
import com.hiperboot.db.entity.ParentTable;
import com.hiperboot.db.repository.MainTableHiperBootRepository;
import com.hiperboot.db.repository.ParentTableHiperBootRepository;

class HiperBootManyToOneTest extends BaseTestClass {

    @Autowired
    private ParentTableHiperBootRepository level01Repository;

    @Autowired
    private MainTableHiperBootRepository mainTableHiperBootRepository;

    @Test
    void testGetByFilter() {
        List<ParentTable> results = level01Repository.getByHiperBootFilter(ParentTable.class, Map.of("someTable", Map.of("id", "1")));
        assertThat(results).isNotEmpty();
    }

    @Test
    void manyToOneWithStringPKTest() {
        List<MainTable> results = mainTableHiperBootRepository.getByHiperBootFilter(MainTable.class, columnSubEntity("childTable.granChild.something", "Nothing3"));
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getChildTable().getGranChild().get(0).getSomething()).isEqualTo("Nothing3");
    }
}
