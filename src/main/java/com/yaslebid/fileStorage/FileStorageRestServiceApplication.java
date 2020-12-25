package com.yaslebid.fileStorage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class })
@EnableElasticsearchRepositories
public class FileStorageRestServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileStorageRestServiceApplication.class, args);
	}

}
