package io.github.yamachi4416.httpfs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import io.github.yamachi4416.httpfs.fs.SubFs;

@SpringBootApplication
public class Application implements WebFluxConfigurer {

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

	@Override
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
		configurer.defaultCodecs().configureDefaultCodec(codec -> {
			if (codec instanceof DefaultPartHttpMessageReader) {
				DefaultPartHttpMessageReader reader = (DefaultPartHttpMessageReader) codec;
				reader.setMaxDiskUsagePerPart(config.getMaxUploadSize().toBytes());
				reader.setMaxParts(config.getMaxUploadParts());
			}
		});
	}

	@Autowired
	public WebFluxProperties webFluxProperties(WebFluxProperties prop) {
		prop.setBasePath(config.getBasePath());
		return prop;
	}
}
