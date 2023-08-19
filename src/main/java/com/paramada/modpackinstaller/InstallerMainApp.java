package com.paramada.modpackinstaller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class InstallerMainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*
        #9a33a6
        #5fd45f
        #ecebeb
        #f2abc7
         */
        FXMLLoader fxmlLoader = new FXMLLoader(InstallerMainApp.class.getResource("main-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        var file = this.getClass().getResource("styles.css");
        assert file != null;
        scene.getStylesheets().add(file.toString());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setTitle("Modpack Installer");
        stage.setIconified(false);
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