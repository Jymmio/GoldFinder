package com.example.goldfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import static com.example.goldfinder.server.AppServer.COLUMN_COUNT;
import static com.example.goldfinder.server.AppServer.ROW_COUNT;

public class Controller {
    private Socket socket = new Socket("localhost", 1234);
    private InputStream is = socket.getInputStream();
    private OutputStream os = socket.getOutputStream();
    private String playerName = "";
    private Stage stage;
    private static final String VIEW_RESOURCE_PATH = "/com/example/goldfinder/gridView.fxml";
    @FXML
    Button play;
    @FXML
    TextField textField;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Canvas gridCanvas;
    @FXML
    HBox hbox;
    @FXML
    VBox vbox;
    @FXML
    Label scoreName;
    @FXML
    Label score;
    @FXML
    Pane pauseEnable;
    Pane pausePane;
    Text pausedText;
    GridView gridView;
    int column, row;
    boolean isPaused;

    public Controller() throws IOException {
    }


    /*

    InputStream is = s.getInputStream();
    OutputStream os = s.getOutputStream();*/
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    public void switchGameScene() throws IOException{
        FXMLLoader loader = new FXMLLoader();
        URL location = AppClient.class.getResource(VIEW_RESOURCE_PATH);
        loader.setLocation(location);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        Controller gameController = loader.getController();
        gameController.setPlayerName(textField.getText());
        gameController.initializeGame();
        root.setOnKeyPressed(gameController::handleMove);
    }
    public void initializeGame() throws IOException {
        this.gridView = new GridView(gridCanvas, COLUMN_COUNT, ROW_COUNT);
        pausePane = pauseEnable;
        score.setText("0");
        scoreName.setText(playerName);
        System.out.println(playerName);
        gridView.repaint();
        column = is.read();
        row = is.read();
        System.out.println("c.row : " + row + " | c.col : " + column);
        gridView.paintPlayerStartPosition(column, row);
        isPaused = false;
        anchorPane.getChildren().remove(pauseEnable);
        if(anchorPane.getChildren().contains(pausedText))
            anchorPane.getChildren().remove(pausedText);
    }

    public void pauseToggleButtonAction(ActionEvent actionEvent) {
        isPaused = true;
        anchorPane.getChildren().add(pausePane);
        double canvasWidth = gridCanvas.getWidth();
        double canvasHeight = gridCanvas.getHeight();

        // Définir le texte à afficher
        String message = "Paused";

        // Créer l'élément Text
        pausedText = new Text(message);

        // Définir le style du texte (vous pouvez ajuster selon vos préférences)
        pausedText.setFill(Color.WHITE);
        pausedText.setFont(Font.font("Arial", FontWeight.BOLD, 96));

        // Positionner le texte au centre du Canvas
        pausedText.setX(vbox.getLayoutBounds().getWidth() + (canvasWidth - pausedText.getLayoutBounds().getWidth()) / 2);
        pausedText.setY((canvasHeight + pausedText.getLayoutBounds().getHeight()) / 2);

        // Ajouter le texte au Canvas
        anchorPane.getChildren().add(pausedText);
        System.out.println(anchorPane.getChildren());
    }

    public void playToggleButtonAction(ActionEvent actionEvent) {
        isPaused = false;
        if(anchorPane.getChildren().contains(pausePane))
            anchorPane.getChildren().remove(pausePane);
        if(anchorPane.getChildren().contains(pausedText))
            anchorPane.getChildren().remove(pausedText);
        String tempScoreText = score.getText();
        Integer tempScore = Integer.valueOf(tempScoreText) + 1;
        score.setText(tempScore.toString());
    }

    public void oneStepButtonAction(ActionEvent actionEvent) {
    }

    public void restartButtonAction(ActionEvent actionEvent) throws IOException {
        initializeGame();
    }

    public void handleMove(KeyEvent keyEvent) {
        if(isPaused)
            return;
        switch (keyEvent.getCode()) {
            case Z -> row = Math.max(0, row - 1);
            case Q -> column = Math.max(0, column - 1);
            case S -> row = Math.min(ROW_COUNT - 1, row + 1);
            case D -> column = Math.min(COLUMN_COUNT - 1, column + 1);
        }
        gridView.repaint();
        gridView.paintToken(column, row);
    }
    public void setPlayerName(String name){
        this.playerName = name;
    }
}

