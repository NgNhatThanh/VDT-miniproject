package org.vdt.qlch.meetinghistoryservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vdt.services")
public record ServiceUrlConfig(String meeting) {
}
