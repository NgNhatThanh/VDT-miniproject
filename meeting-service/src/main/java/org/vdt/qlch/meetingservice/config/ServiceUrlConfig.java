package org.vdt.qlch.meetingservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vdt.services")
public record ServiceUrlConfig(String user) {
}
