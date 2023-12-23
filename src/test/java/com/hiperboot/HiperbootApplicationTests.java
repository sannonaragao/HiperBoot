package com.hiperboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EntityScan(basePackages = "com.hiperboot")
class HiperbootApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("teste");
    }
}