package com.bluemsun.know;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@SpringBootApplication
@EnableTransactionManagement
@SpringBootApplication
@ComponentScan("com.bluemsun.know.*")
@EnableScheduling
public class KnowApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowApplication.class, args);
    }

}
