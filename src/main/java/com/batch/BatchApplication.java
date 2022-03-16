package com.batch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/*
 * このアプリは
 * kafka経由での非同期振込処理本体を担う処理です
 */
@EnableAsync
@SpringBootApplication
@ComponentScan("com.batch.domain.repos")
@ComponentScan("com.batch.domain.entity")
@ComponentScan("com.batch.domain")
@ComponentScan("com.batch.app")
@ComponentScan("com.batch.app.message")
//@EnableJpaRepositories(basePackages = { "com.batch.infra.repos" })
@ComponentScan
public class BatchApplication {
	public static void main(String[] args) {
		try {
			SpringApplication.run(BatchApplication.class, args);
			System.out.println("----- App is started -----");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
