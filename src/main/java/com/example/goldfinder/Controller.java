package com.example.goldfinder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
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

import static com.example.goldfinder.server.AppServer.COLUMN_COUNT;
import static com.example.goldfinder.server.AppServer.ROW_COUNT;

public class Controller {
    @FXML
    AnchorPane anchorPane;
    @FXML
    Canvas gridCanvas;
    @FXML
    HBox hbox;
    @FXML
    VBox vbox;
    @FXML
    Label score;
    @FXML
    Pane pauseEnable;
    Pane pausePane;
    Text pausedText;
    GridView gridView;
    int column, row;
    boolean isPaused;

    public void initialize() {
        this.gridView = new GridView(gridCanvas, COLUMN_COUNT, ROW_COUNT);
        pausePane = pauseEnable;
        score.setText("0");
        gridView.repaint();
        column = 10; row = 10;
        isPaused = false;
        gridView.paintToken(column, row);
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

    public void restartButtonAction(ActionEvent actionEvent) {
        initialize();
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
}

