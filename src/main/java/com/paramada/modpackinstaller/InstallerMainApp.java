package com.paramada.modpackinstaller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class InstallerMainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException, XmlPullParserException {
        /*
        #9a33a6
        #5fd45f
        #ecebeb
        #f2abc7
         */
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = reader.read(new FileReader("pom.xml"));
        Globals.INSTALLER_VERSION = model.getVersion();
        FXMLLoader fxmlLoader = new FXMLLoader(InstallerMainApp.class.getResource("main-screen.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        var file = this.getClass().getResource("resources/styles.css");
        assert file != null;
        scene.getStylesheets().add(file.toString());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setTitle("Modpack Installer - %s".formatted(Globals.INSTALLER_VERSION));
        stage.setIconified(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(InstallerMainApp.class.getResourceAsStream("resources/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        try (InputStream inputStream = InstallerMainApp.class.getResourceAsStream("resources/config.conf")) {
            props.load(inputStream);
            Globals.properties = props;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        launch();
    }
}