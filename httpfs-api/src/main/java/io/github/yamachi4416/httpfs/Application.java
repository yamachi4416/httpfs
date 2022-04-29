package io.github.yamachi4416.httpfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.github.yamachi4416.httpfs.fs.SubFs;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	AppConfig config;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public SubFs subFs() {
		var root = config.getDocumentRootPath();
		logger.info("Files DocumentRoot {}", root);
		return new SubFs(root);
	}
}
