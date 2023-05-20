package ru.link.extractor.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import reactor.netty.http.server.HttpServer;

@AllArgsConstructor
public class PortCustomizer implements NettyServerCustomizer {
    private final int port;
    @Override
    public HttpServer apply(HttpServer httpServer) {
        return httpServer.port(port);
    }

}
