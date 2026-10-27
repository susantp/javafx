package com.techbizz.javafx;

import com.sun.net.httpserver.HttpHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class HelloController {
    private final HttpClient client = HttpClient.newHttpClient();

    @FXML
    private Label welcomeText;

    @FXML
    private Label requestedOn;

    @FXML
    protected void onHelloButtonClick() {
        this.getPublicIpAddress()
                .thenAccept(ip -> Platform.runLater(() -> {
                    requestedOn.setText(this.getCurrentDateTime().concat(" \n Request by IP: ").concat(ip));
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        requestedOn.setText(this.getCurrentDateTime() + " IP unavailable");
                    });
                    return null;
                });
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private CompletableFuture<String> getPublicIpAddress() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ipify.org"))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new CompletionException(
                                new IOException("Http Error: " + response.statusCode())
                        );
                    }
                    return response.body().trim();
                });
    }
}
