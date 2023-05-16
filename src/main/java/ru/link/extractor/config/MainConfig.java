package ru.link.extractor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.link.extractor.component.ChannelExplorer;
import ru.link.extractor.component.VideoLinkExtractor;

@Configuration
public class MainConfig {

    private final Integer maxAttempts;
    private final Long waitPeriod;
    private final String defaultChannel;
    private final Integer port;

    public MainConfig(@Autowired ApplicationArguments arguments, @Autowired DefaultConfig defaultConfig) {
        this.maxAttempts = arguments.containsOption("max-attempts")
                ? Integer.valueOf(arguments.getOptionValues("max-attempts").get(0))
                : defaultConfig.getDefaultMaxAttempts();

        this.waitPeriod = arguments.containsOption("wait-period")
                ? Long.valueOf(arguments.getOptionValues("wait-period").get(0))
                : defaultConfig.getDefaultWaitPeriod();

        this.defaultChannel = arguments.containsOption("default-channel")
                ? arguments.getOptionValues("default-channel").get(0)
                : defaultConfig.getDefaultChannel();

        this.port = arguments.containsOption("port")
                ? Integer.valueOf(arguments.getOptionValues("port").get(0))
                : defaultConfig.getDefaultPort();

    }

    @Bean
    public VideoLinkExtractor getVideoLinkExtractor() {
        return new VideoLinkExtractor(maxAttempts, waitPeriod);
    }

    @Bean
    public ChannelExplorer getChannelExplorer(VideoLinkExtractor extractor) {
        return new ChannelExplorer(extractor, defaultChannel);
    }

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory webServerFactory = new NettyReactiveWebServerFactory();
        webServerFactory.addServerCustomizers(new PortCustomizer(port));
        return webServerFactory;
    }
}
