package com.techbizz.javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Objects;

public class AgeController {
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Button calculateAge;

    @FXML
    private Label ageLabel;

    @FXML
    private Hyperlink goToHome;

    public void initialize() {
        this.calculateAge.disableProperty().bind(
                birthDatePicker.valueProperty().isNull()
        );
        hourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12)
        );

        minuteSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0)
        );
    }

    @FXML
    protected void calculateAge() {
        LocalDate birthdate = birthDatePicker.getValue();
        LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
        LocalDateTime birthDateTime = LocalDateTime.of(birthdate, time);
        if (birthDateTime.isAfter(LocalDateTime.now())) {
            ageLabel.setText("Date and time cannot be in the future.");
            return;
        }
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        ageLabel.setText("Date of Birth: " + birthDateTime + "\nAge: " + age + " years ");
    }

    @FXML
    protected void onGoHome() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Scene scene = goToHome.getScene();
        scene.setRoot(root);
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle("Welcome");
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}
