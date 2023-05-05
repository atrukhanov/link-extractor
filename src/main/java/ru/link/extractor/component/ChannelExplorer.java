package ru.link.extractor.component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.link.extractor.model.Response;


@AllArgsConstructor
@Slf4j
public class ChannelExplorer {
    private VideoLinkExtractor extractor;
    private String defaultChannel;
    private static final String LINK_REGEX = "\\bhttps?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    private static final WebClient WEB_CLIENT = WebClient.builder().baseUrl("https://t.me/s/").build();

    public Mono<Response> processTgChannel(String pathChannel) {
        String selectedChannel = Objects.isNull(pathChannel) ? defaultChannel : pathChannel;
        Mono<String> response = WEB_CLIENT.get().uri(selectedChannel).exchangeToMono((r) -> {
            if (!r.statusCode().equals(HttpStatus.OK)) {
                if (r.statusCode().is4xxClientError()) {
                    log.error("Content not found, {}", r.statusCode());
                } else if (r.statusCode().is5xxServerError()) {
                    log.error("Server t.me not answer, {}", r.statusCode());
                }
                log.error("Unexpected error. Status {}. " , r.statusCode());
            }
            return r.bodyToMono(String.class);
        });
        return response.map(this::extractData);
    }

    public Response extractData(String webData) {
        Element message = Objects.requireNonNull(Jsoup
                .parse(webData)
                .select("div.tgme_widget_message_wrap").last());

       String content = Objects.requireNonNull(message
               .select("div.tgme_widget_message_text").last()).text();
       Matcher matcher = Pattern.compile(LINK_REGEX).matcher(content);
       if (matcher.find()) {
            String result = matcher.group();
            log.info("Found link {}", result);
            return extractor.getVideoLink(result);
        } else {
            log.error("Not found link");
            return new Response(false);
        }
    }
}
