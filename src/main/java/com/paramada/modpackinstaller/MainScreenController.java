package com.paramada.modpackinstaller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONObject;

public class MainScreenController {
    @FXML
    public FlowPane modsList;
    @FXML
    public ChoiceBox<String> modpackSelector;
    @FXML
    public ImageView placeholderImage;
    @FXML
    public Rectangle2D placeholderViewPort;
    public Button nextBtn;


    @FXML
    public void initialize() {
        String modpacksStr = Globals.properties.getProperty("app.modpacks");
        List<String> modpacks = Stream.of(modpacksStr.split(",")).map(String::trim).toList();

        modpackSelector.getItems().setAll(modpacks);
        modpackSelector.getSelectionModel().selectedItemProperty().addListener((item) -> {
            HttpClient httpClient = HttpClient.newHttpClient();
            String modPack = ((ReadOnlyObjectProperty<?>) item).getValue().toString();
            String selected = modPack.toLowerCase();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Globals.properties.getProperty("app.modpacks_url")))
                    .header("Content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"mod_pack\":\"%s\"}".formatted(selected)))
                    .build();
            modsList.getChildren().clear();

            HttpResponse<String> response = null;
            try {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                int statusCode = response.statusCode();
                if (statusCode == 200) {
                    String responseBody = response.body();
                    JSONObject object = new JSONObject(responseBody);
                    object.getJSONArray("mods").iterator().forEachRemaining((it) -> {
                        JSONObject jsonObject = (JSONObject) it;
                        modsList.getChildren().add(new Label("%s - %s".formatted(jsonObject.getString("mod_name"), jsonObject.getString("version"))));
                    });

                    Globals.modPackData = object;
                    Globals.modpackName = modPack;
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        nextBtn.setDisable(false);

        placeholderImage.setPreserveRatio(true);
        placeholderImage.setViewport(placeholderViewPort);

    }

    public void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
    }

    public void nextScreen(ActionEvent actionEvent) throws IOException {

        Stage window = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(InstallerMainApp.class.getResource("install-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().addAll(window.getScene().getStylesheets());
        window.setScene(scene);
    }
}