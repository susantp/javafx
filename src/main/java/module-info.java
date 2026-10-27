module com.techbizz.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.net.http;
    requires jdk.httpserver;

    opens com.techbizz.javafx to javafx.fxml;
    exports com.techbizz.javafx;
}