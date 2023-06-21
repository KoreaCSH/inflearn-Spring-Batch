package com.springbatch.practice;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


// 반드시 @EnableBatchProcessing 애노테이션을 추가해야 한다.
// 스프링 스케줄링을 활용하기 위해서는 @EnableScheduling 애노테이션을 추가해야 한다.
@EnableScheduling
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchTutorialApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchTutorialApplication.class, args);
	}

}
