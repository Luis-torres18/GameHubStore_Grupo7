package com.GameHubStore.shipping.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.configuration.SpringDocHateoasConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = {SpringDocHateoasConfiguration.class})
public class SpringDocConfig {
}