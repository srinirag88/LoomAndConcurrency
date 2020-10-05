package org.example.concurrency;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

import static java.lang.System.Logger.Level.DEBUG;

public class DownloadParallel {
    private static final System.Logger LOGGER = System.getLogger(DownloadParallel.class.getName());

    public static void main(String[] args) {
        downloadParallel();
    }

    public static void downloadParallel() {
        final var urls = List.of("https://www.google.com", "https://edition.cnn.com");
        var deadline = Instant.now().plusSeconds(100);
        final var exec = Executors.newVirtualThreadExecutor().withDeadline(deadline);
        final var httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .executor(exec)
                .build();

        try (exec) {
            final var tasks = new ArrayList<CompletableFuture<String>>();
            for (final var url : urls) {
                final var request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .build();
                LOGGER.log(DEBUG, "Downloading the content for the url " + url);
                tasks.add(httpClient.sendAsync(request, BodyHandlers.ofString())
                        .thenApply(r -> handleResponse(r, url)));
            }
            tasks.stream().map(CompletableFuture::join).forEach(System.out::println);
        } catch (CompletionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    static String handleResponse(HttpResponse<String> httpResponse, String url) {
        if (httpResponse.statusCode() == 200) {
            System.out.println("##### Downloaded content for the url " + url);
            return httpResponse.body();
        } else {
            return null;
        }
    }
}
