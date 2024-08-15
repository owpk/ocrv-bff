package com.ocrf.bff;

import com.ocrf.bff.config.props.PropertyConfig;
import com.ocrf.bff.config.props.ResourceServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({PropertyConfig.class, ResourceServerProperties.class})
public class BffApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffApplication.class, args);
	}

}
