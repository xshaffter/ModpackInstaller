package com.paramada.modpackinstaller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class FinalizationController {

    @FXML
    public ImageView placeholderImage;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            placeholderImage.fitWidthProperty().bind(placeholderImage.getScene().widthProperty());

        });
    }

    public void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }
}
