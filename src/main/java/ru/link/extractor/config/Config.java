package ru.link.extractor.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.link.extractor.component.ChannelExplorer;
import ru.link.extractor.component.VideoLinkExtractor;

@Data
@Configuration
public class Config {
    @Value("${retry-policy.max-attempts}")
    Integer maxAttempts;
    @Value("${retry-policy.back-off-period}")
    Long backOffPolicy;
    @Value("${default.channel}")
    String defaultChannel;

    @Bean
    VideoLinkExtractor getVideoLinkExtractor() {
        return new VideoLinkExtractor(this.maxAttempts, this.backOffPolicy);
    }

    @Bean
    ChannelExplorer getChannelExplorer(VideoLinkExtractor extractor) {
        return new ChannelExplorer(extractor, this.defaultChannel);
    }
}
