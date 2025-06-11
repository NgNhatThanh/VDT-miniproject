package org.vdt.qlch.speechservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vdt.services")
public record ServiceUrlConfig(String user) {
}
