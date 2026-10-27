package com.techbizz.javafx;

import com.sun.net.httpserver.HttpHandler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    private TextField host;
    @FXML
    private Button requestButton;
    @FXML
    private Label helperText;

    public void initialize() {
        welcomeText.setText("Enter the url host");
        helperText.setText("e.g. api.ipify.org");
        helperText.setStyle("-fx-text-fill: grey; -fx-font-size: 10px;");
        requestButton.disableProperty().bind(
                host.textProperty().isEmpty()
        );
    }

    @FXML
    protected void onHelloButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Alert alertE = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Request");
        alert.setHeaderText("Public IP");
        alertE.initOwner(welcomeText.getScene().getWindow());
        alert.initOwner(welcomeText.getScene().getWindow());
        String host = this.host.getText();
        if (!this.isValidHost(host)) {
            alertE.setHeaderText("Invalid Host");
            alertE.showAndWait();
            return;
        }
        this.getPublicIpAddress(host)
                .thenAccept(ip -> Platform.runLater(() -> {
                    alert.setContentText(String.join("\n", "Request: ".concat(ip), "On: ".concat(this.getCurrentDateTime())));
                    alert.initOwner(welcomeText.getScene().getWindow());
                    alert.showAndWait();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        alertE.setContentText("IP unavailable !!!");
                        alertE.showAndWait();
                    });
                    return null;
                });
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    private CompletableFuture<String> getPublicIpAddress(String host) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://%s".formatted(host)))
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

    private boolean isValidHost(String host) {
        if (host == null || host.isBlank()) {
            return false;
        }

        String hostnameRegex =
                "^(?=.{1,253}$)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
                        "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

        return host.matches(hostnameRegex);
    }
}
