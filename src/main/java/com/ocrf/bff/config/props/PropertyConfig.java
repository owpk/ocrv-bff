package com.ocrf.bff.config.props;

import com.ocrf.bff.config.dto.Service;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;


@Getter
@Setter
@ToString
//@Configuration
@ConfigurationProperties(prefix = "bff")
public class PropertyConfig {

    Map<String, Service> service;
}
