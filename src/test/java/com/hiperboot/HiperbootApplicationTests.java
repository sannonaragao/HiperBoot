package com.hiperboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

//@Profile("h2")
@SpringBootTest
@EntityScan(basePackages = "com.hiperboot.db.entity")
class HiperbootApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("teste");
    }
}