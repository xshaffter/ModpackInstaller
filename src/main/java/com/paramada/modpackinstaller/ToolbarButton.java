package com.paramada.modpackinstaller;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class ToolbarButton extends Button {
    public ToolbarButton(String labelText) {
        super(labelText);

        this.setPrefHeight(29);
        this.prefWidthProperty().bind(this.heightProperty());

        this.getStyleClass().add("toolbar-btn");
    }
}
