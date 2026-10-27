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

    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private final Alert alertE = new Alert(Alert.AlertType.ERROR);

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
        String hostname = this.host.getText();
        if (!this.isValidHost(hostname)) {
            alertE.showAndWait();
            return;
        }
        getPublicIpAddress(hostname)
                .whenComplete((ip, ex) -> Platform.runLater(() -> {

                    if (ex != null) {
                        showAlert(
                                Alert.AlertType.ERROR,
                                "Request Failed",
                                "Unable to connect to: " + hostname
                                        + "\nReason: " + ex.getMessage()
                        );
                        return;
                    }

                    showAlert(
                            Alert.AlertType.INFORMATION,
                            "Public IP",
                            "Response: " + ip
                                    + "\nOn: " + getCurrentDateTime()
                    );
                }));
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

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message
    ) {
        Alert dialog = new Alert(type);

        dialog.setTitle("HTTP Request");
        dialog.setHeaderText(title);
        dialog.setContentText(message);
        dialog.initOwner(welcomeText.getScene().getWindow());

        dialog.showAndWait();
    }
}
