package ru.link.extractor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class DefaultConfig {

    @Value("${default.max-attempts}")
    private Integer defaultMaxAttempts;

    @Value("${default.wait-period}")
    private Long defaultWaitPeriod;

    @Value("${default.channel}")
    private String defaultChannel;

    @Value("${default.port}")
    private Integer defaultPort;
}
