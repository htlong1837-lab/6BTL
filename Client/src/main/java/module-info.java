module com.auction.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.auction.client to javafx.graphics, javafx.fxml;
    opens com.auction.client.model to javafx.base;
    opens com.auction.controller to javafx.fxml;
}
