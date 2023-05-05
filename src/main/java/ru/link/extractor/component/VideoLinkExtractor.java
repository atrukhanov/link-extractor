package ru.link.extractor.component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.link.extractor.model.Response;

@AllArgsConstructor
public class VideoLinkExtractor {
    private static final Logger log = LoggerFactory.getLogger(VideoLinkExtractor.class);
    private Integer maxAttempts;
    private Long backOffPolicy;
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    public Response getVideoLink(String link) {
        this.taskScheduler.initialize();
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("yt-dlp", "-f", "best", "-g", link, "2  >&1");
        CountDownLatch counter = new CountDownLatch(this.maxAttempts);
        AtomicBoolean isLinkExist = new AtomicBoolean(false);

        try {
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            ScheduledFuture<?> future = this.taskScheduler.scheduleWithFixedDelay(() -> {
                try {
                    this.checkData(inputStream, isLinkExist);
                    counter.countDown();
                } catch (IOException var5) {
                    counter.countDown();
                    throw new RuntimeException(var5);
                }
            }, Duration.ofMillis(this.backOffPolicy));
            counter.await((long)this.maxAttempts * this.backOffPolicy, TimeUnit.MILLISECONDS);
            future.cancel(true);
            return isLinkExist.get()
                    ? new Response(true, (new String(inputStream.readAllBytes(), StandardCharsets.US_ASCII)).replaceFirst(String.valueOf('\n'), ""))
                    : new Response(false);
        } catch (InterruptedException | IOException e) {
            log.error("yt-dlp execute error");
            e.printStackTrace();
            return new Response(false);
        }
    }

    private void checkData(InputStream inputStream, AtomicBoolean flag) throws IOException {
        flag.set(inputStream.available() > 0);
    }
}
