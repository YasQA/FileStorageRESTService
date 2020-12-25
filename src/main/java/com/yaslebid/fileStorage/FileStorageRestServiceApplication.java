package com.yaslebid.fileStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class })
@EnableElasticsearchRepositories
public class FileStorageRestServiceApplication {

	public static final Logger LOGGER =
			LoggerFactory.getLogger(FileStorageRestServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FileStorageRestServiceApplication.class, args);
	}

}
