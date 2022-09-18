package com.bank.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
public class BankApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        log.info("Bank App Server");
        SpringApplication.run(BankApplication.class, args);
    }
}
