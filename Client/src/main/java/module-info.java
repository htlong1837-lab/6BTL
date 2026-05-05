module com.auction.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.auction.client to javafx.graphics, javafx.fxml;
    opens com.auction.client.dto to javafx.fxml, com.google.gson;
    opens com.auction.controller to javafx.fxml;
}
