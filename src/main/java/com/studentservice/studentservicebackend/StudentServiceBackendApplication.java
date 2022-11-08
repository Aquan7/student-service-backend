package com.studentservice.studentservicebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.studentService.studentServiceBackend.mapper")
@EnableScheduling
public class StudentServiceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceBackendApplication.class, args);
    }

}
