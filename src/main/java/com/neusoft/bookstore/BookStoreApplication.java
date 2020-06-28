package com.neusoft.bookstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
@EnableTransactionManagement
@MapperScan("com.neusoft.bookstore.*.mapper")
public class BookStoreApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BookStoreApplication.class);
    }
}
