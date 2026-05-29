package com.ghost.server.domain.run.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ghost.run")
public record RunProperties(int idleThresholdSeconds, int scanIntervalSeconds) {
}
