package com.paramada.modpackinstaller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class InstallerScreenController {

    @FXML
    public Label LOGGER;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button nextButton;
    private final String dotMinecraft;
    private final String modsFolder;
    @FXML
    public ImageView placeholderImage;

    public InstallerScreenController() {
        final String appdataFolder = System.getenv("appdata");
        dotMinecraft = Paths.get(appdataFolder, ".minecraft").toString();
        modsFolder = Paths.get(dotMinecraft, "mods").toString();
    }


    private void log(final String text) {
        Platform.runLater(() -> {
            LOGGER.setText("%s".formatted(text));
        });
    }

    private final Task<Integer> installation = new Task<>() {
        @Override
        protected Integer call() throws Exception {
            JSONArray mods = Globals.modPackData.getJSONArray("mods");
            long max = mods.length();
            updateProgress(0, max + 2);

            cleanModsFolder();
            updateProgress(1, max + 2);
            var loader = Globals.modPackData.getJSONObject("loader");

            installModLoader(loader.getString("name"), loader.getString("version"));
            updateProgress(2, max + 2);

            for (int workedMod = 0; workedMod < max; workedMod++) {
                var mod = mods.getJSONObject(workedMod);
                final String modName = mod.getString("mod_name");
                final String version = mod.getString("version");
                final String url = tryGet(mod, "file_url");

                log("Downloading %s - %s".formatted(modName, version));
                downloadMod(modName, version, url);
                log("Installed %s".formatted(modName));
                updateProgress(workedMod + 1 + 2, max + 2);
            }

            log("Installation finished...");

            nextButton.setDisable(false);

            return null;
        }
    };

    @SuppressWarnings("SameParameterValue")
    private String tryGet(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException ignored) {
            return null;
        }
    }

    private void downloadFabricInstaller() throws MalformedURLException {
        var installerFile = new File("fabric-installer.jar");
        if (installerFile.exists()) {
            installerFile.delete();
        }

        var downloadURL = URI.create(Globals.properties.getProperty("app.fabric-installer-url")).toURL();

        try (var inputStream = new BufferedInputStream(downloadURL.openStream())) {
            Files.copy(inputStream, installerFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log("error downloading installer");
        }

    }

    private void installModLoader(final String loader, final String version) throws IOException {
        downloadFabricInstaller();
        Runtime.getRuntime().exec("java -jar %s client -dir \"%s/.minecraft\" -mcversion %s -noprofile".formatted(
                "%s-installer.jar".formatted(loader)
                , System.getenv("appdata"), version));
        createProfile();

        log("Installed %s - %s mod loader".formatted(loader, version));
    }

    private void createProfile() throws IOException {
        var jsonFile = Paths.get(dotMinecraft, "launcher_profiles.json").toFile();
        var profileFile = this.getClass().getResource("resources/profile.json").openStream();
        var tokener = new JSONTokener(new FileReader(jsonFile));
        var singleProfiletokener = new JSONTokener(profileFile);
        var profilesJson = new JSONObject(tokener);


        var singleProfileVersion = new JSONObject(singleProfiletokener);
        var profilesElement = profilesJson.getJSONObject("profiles");

        profilesElement.put("MaryMods", singleProfileVersion);
        profilesJson.put("profiles", profilesElement);

        var writer = new FileWriter(jsonFile);
        writer.write(profilesJson.toString(4));
        writer.flush();
        writer.close();

    }

    private void downloadMod(String modName, String version, String url) throws InterruptedException, IOException {
        if (url != null) {
            downloadFile(url, Paths.get(modsFolder, "%s.jar".formatted(modName)));
            return;
        }


        final String consumeTemplate = "https://api.modrinth.com/v2/project/%s/version";

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(consumeTemplate.formatted(modName)))
                .header("Content-type", "application/json")
                .GET()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            var object = new JSONArray(responseBody);
            object.iterator().forEachRemaining(it -> {
                var jsonObject = (JSONObject) it;
                if (jsonObject.getString("version_number").equals(version) && arrayContains(jsonObject.getJSONArray("loaders"), "fabric")) {
                    try {
                        String downloadURL = jsonObject.getJSONArray("files").getJSONObject(0).getString("url");
                        downloadFile(downloadURL, Paths.get(modsFolder, "%s.jar".formatted(modName)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            System.out.printf("%s-%s%n", modName, version);
            System.out.println(response.statusCode());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private boolean arrayContains(JSONArray loaders, String expectedLoader) {
        for (Object it : loaders) {
            if (it.toString().equals(expectedLoader)) {
                return true;
            }

        }
        return false;
    }

    public static void downloadFile(String fileUrl, Path targetPath) throws IOException {
        URL url = URI.create(fileUrl).toURL();
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void cleanModsFolder() throws IOException {
        log("Backing up mods folder");

        var files = Paths.get(modsFolder).toFile().listFiles();
        boolean created = Paths.get(modsFolder, "backup").toFile().mkdir();
        if (created) {
            log("backup folder created");
        }
        assert files != null;
        var nonFolderFiles = Arrays.stream(files).filter(it -> !it.isDirectory()).toList();
        if (nonFolderFiles.isEmpty()) {
            return;
        }

        for (var file : nonFolderFiles) {
            Files.move(file.toPath(), Paths.get(modsFolder, "backup", file.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            progressBar.prefWidthProperty().bind(progressBar.getScene().widthProperty());
            placeholderImage.fitWidthProperty().bind(placeholderImage.getScene().widthProperty());

            progressBar.progressProperty().bind(installation.progressProperty());
            new Thread(installation, "Installation-fx").start();
        });
    }

    public void closeWindow(ActionEvent actionEvent) {
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
        installation.cancel();
    }

    public void nextScreen(ActionEvent actionEvent) throws IOException {
        Stage window = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(InstallerMainApp.class.getResource("finalization-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().addAll(window.getScene().getStylesheets());
        window.setScene(scene);
    }
}