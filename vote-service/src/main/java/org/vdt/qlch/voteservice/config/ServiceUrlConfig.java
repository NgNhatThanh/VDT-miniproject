package org.vdt.qlch.voteservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vdt.services")
public record ServiceUrlConfig(String meeting, String document, String user) {
}
