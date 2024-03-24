package com.example.goldfinder;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

public class AppClient extends javafx.application.Application {
    private static final String START_RESOURCE_PATH = "/com/example/goldfinder/gameStart.fxml";
    private static final String APP_NAME = "Gold Finder";


    private Stage primaryStage;
    private Parent view;
    private void initializePrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(APP_NAME);
        this.primaryStage.setOnCloseRequest(event -> Platform.exit());
        this.primaryStage.setResizable(true);
        this.primaryStage.sizeToScene();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initializePrimaryStage(primaryStage);
        initializeView();
        showScene();
    }




    private void initializeView() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL location = AppClient.class.getResource(START_RESOURCE_PATH);
        loader.setLocation(location);
        view = loader.load();
        Controller controller = loader.getController();
        controller.setStage(this.primaryStage);
    }

    private void showScene() {
        Scene scene = new Scene(view);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}