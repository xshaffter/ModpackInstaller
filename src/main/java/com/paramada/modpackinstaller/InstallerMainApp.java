package com.paramada.modpackinstaller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class InstallerMainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InstallerMainApp.class.getResource("main-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("Modpack Installer");
        stage.setAlwaysOnTop(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(InstallerMainApp.class.getResourceAsStream("icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream inputStream = InstallerMainApp.class.getResourceAsStream("config.conf")) {
            props.load(inputStream);
            Globals.properties = props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        launch();
    }
}