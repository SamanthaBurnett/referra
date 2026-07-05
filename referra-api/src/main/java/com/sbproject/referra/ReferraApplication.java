package com.sbproject.referra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class ReferraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReferraApplication.class, args);
    }

}
