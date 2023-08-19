package com.paramada.modpackinstaller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Toolbar extends FlowPane {

    private double xOffset = 0, yOffset = 0;

    public Toolbar() {
        this.setOrientation(Orientation.HORIZONTAL);
        this.setRowValignment(VPos.TOP);
        this.setAlignment(Pos.TOP_LEFT);
        this.setStyle("-fx-background-color:#9a33a6");

        var imageView = new ImageView(String.valueOf(this.getClass().getResource("icon.png")));

        imageView.setPreserveRatio(true);
        imageView.setFitWidth(30);
        this.getChildren().add(imageView);

        var actionsLayout = new FlowPane(Orientation.HORIZONTAL);
        actionsLayout.setAlignment(Pos.TOP_RIGHT);

        var closeBtn = new ToolbarButton("X");
        var minimizeBtn = new ToolbarButton("―");

        closeBtn.setOnAction(this::closeWindow);
        minimizeBtn.setOnAction(this::minimizeWindow);

        actionsLayout.getChildren().add(minimizeBtn);
        actionsLayout.getChildren().add(closeBtn);

        Platform.runLater(() -> {
            actionsLayout.prefWidthProperty().bind(this.getScene().widthProperty().subtract(imageView.fitWidthProperty()));
        });

        this.getChildren().add(actionsLayout);

        this.setOnMousePressed(this::handleClickAction);
        this.setOnMouseDragged(this::handleMovementAction);

    }

    private void minimizeWindow(ActionEvent actionEvent) {
        Stage window = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        window.setIconified(true);
    }

    private void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    private void handleClickAction(MouseEvent event) {
        Stage window = (Stage) this.getScene().getWindow();
        xOffset = window.getX() - event.getScreenX();
        yOffset = window.getY() - event.getScreenY();
    }
    private void handleMovementAction(MouseEvent event) {
        Stage window = (Stage) this.getScene().getWindow();

        window.setX(event.getScreenX() + xOffset);
        window.setY(event.getScreenY() + yOffset);

    }

}
