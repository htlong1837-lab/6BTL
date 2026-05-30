package com.auction.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class NotificationPopup {

    public static void showBanned(Runnable onClose) {
        show("⛔ Tài khoản bị khóa",
             "Admin đã khóa tài khoản của bạn.\nBạn sẽ bị đăng xuất.",
             "#c0392b", onClose);
    }

    public static void showAuctionDeleted(Runnable onClose) {
        show("🗑 Phiên đấu giá bị xóa",
             "Admin đã xóa phiên đấu giá này.\nCửa sổ sẽ đóng lại.",
             "#e67e22", onClose);
    }

    private static void show(String title, String message, String color, Runnable onClose) {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.setAlwaysOnTop(true);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: white;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #ffe8e8; -fx-wrap-text: true;");
        msgLabel.setMaxWidth(280);
        msgLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button okBtn = new Button("OK");
        okBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 28;" +
            "-fx-background-radius: 4;" +
            "-fx-cursor: hand;"
        );
        okBtn.setOnAction(e -> {
            popup.close();
            if (onClose != null) onClose.run();
        });

        VBox box = new VBox(14, titleLabel, msgLabel, okBtn);
        box.setAlignment(Pos.CENTER);
        box.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-padding: 28 32;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);"
        );
        box.setPrefWidth(340);

        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.show();

        // Auto-close sau 8 giây nếu người dùng không bấm
        new Timeline(new KeyFrame(Duration.seconds(8), e -> {
            if (popup.isShowing()) {
                popup.close();
                if (onClose != null) onClose.run();
            }
        })).play();
    }
}
