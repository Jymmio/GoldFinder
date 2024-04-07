package com.example.goldfinder;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.goldfinder.server.AppServer.COLUMN_COUNT;
import static com.example.goldfinder.server.AppServer.ROW_COUNT;
import static java.lang.Thread.sleep;

public class Controller {
    ConnectedPlayer connectedPlayer;

    private static final String VIEW_RESOURCE_PATH = "/com/example/goldfinder/gridView.fxml";

    public GridView gridView;
    PrintWriter pw;
    BufferedReader br;
    String upSurrounding;
    String downSurrounding;
    String leftSurrounding;
    String rightSurrounding;
    PlayerClient player;
    @FXML
    Button play;
    @FXML
    Button leaderboardButton;
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
    Pane startPane;
    @FXML
    Pane endPane;
    @FXML
    Pane pausePane;
    @FXML
    Pane leaderBoardPane;
    Text pausedText;
    @FXML
    Text leaderTitle;
    @FXML
    TableView<String> tableView;
    @FXML
    TableView<String> tableView2;
    @FXML
    TableColumn<String,String> nameTable;
    @FXML
    TableColumn<String,String> scoreTable;


    boolean isPaused;
    BotPlayer botPlayer;

    public Controller() throws IOException {
    }

    public void setPwAndBr(PrintWriter pw, BufferedReader br){
        this.pw = pw;
        this.br = br;
    }
    public void showNoLeader(){
        anchorPane.getChildren().remove(startPane);
        anchorPane.getChildren().add(leaderBoardPane);
        this.leaderTitle.setText("NO LEADERS :(");
    }
    public void showLeaders(String[] liste){
        anchorPane.getChildren().remove(startPane);
        anchorPane.getChildren().add(leaderBoardPane);
        List<String> names = new ArrayList<String>();
        List<String> scores = new ArrayList<String>();
        for(String string:liste){
            if(string!=null && !string.equals("END")){
                names.add(string.split(":")[1]);
                scores.add(string.split(":")[2]);
            }
        }
        ObservableList<String> observableNames = FXCollections.observableArrayList(names);
        ObservableList<String> observableScores = FXCollections.observableArrayList(scores);

        nameTable.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        scoreTable.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        tableView.getSelectionModel().setCellSelectionEnabled(false);
        tableView2.getSelectionModel().setCellSelectionEnabled(false);
        tableView.setItems(observableNames);
        tableView2.setItems(observableScores);

    }
    public void setConnectedPlayer(ConnectedPlayer connectedPlayer){
        this.connectedPlayer = connectedPlayer;
    }
    public void setPlayer(String name) throws IOException {
        connectedPlayer.setPlayer(name);
    }
    public void initializeGame() throws IOException {
        anchorPane.getChildren().remove(hbox);
        anchorPane.getChildren().remove(endPane);
        anchorPane.getChildren().remove(pausePane);
        anchorPane.getChildren().remove(leaderBoardPane);
        if(!anchorPane.getChildren().contains(startPane)){
            anchorPane.getChildren().add(startPane);
        }
    }
    public void showLoading(){

    }
    @FXML
    public void startGame() throws IOException, InterruptedException {
        anchorPane.getChildren().remove(startPane);
        anchorPane.getChildren().add(hbox);
        this.gridView = new GridView(gridCanvas, COLUMN_COUNT, ROW_COUNT);
        score.setText("0");
        scoreName.setText(connectedPlayer.player.getName());
        gridView.repaint(connectedPlayer.player.x,connectedPlayer.player.y,null);
        isPaused = false;
        score.setText(connectedPlayer.player.scoreProperty().getValue().toString());
        score.textProperty().bind(connectedPlayer.player.scoreProperty().asString());
        connectedPlayer.sendSurroundingRequest();
        /*botPlayer = new BotPlayer(this.connectedPlayer);
        botPlayer.start(); //TASK 3*/
    }
    public void shutDownBot(){
        if(botPlayer != null){
            botPlayer.stop();
        }
    }
    public void pauseToggleButtonAction(ActionEvent actionEvent) {
        isPaused = true;
        anchorPane.getChildren().add(pausePane);
        double canvasWidth = gridCanvas.getWidth();
        double canvasHeight = gridCanvas.getHeight();
        String message = "Paused";
        pausedText = new Text(message);
        pausedText.setFill(Color.WHITE);
        pausedText.setFont(Font.font("Arial", FontWeight.BOLD, 96));
        pausedText.setX(vbox.getLayoutBounds().getWidth() + (canvasWidth - pausedText.getLayoutBounds().getWidth()) / 2);
        pausedText.setY((canvasHeight + pausedText.getLayoutBounds().getHeight()) / 2);
        anchorPane.getChildren().add(pausedText);
    }

    public void playToggleButtonAction(ActionEvent actionEvent) {
        isPaused = false;
        anchorPane.getChildren().remove(pausePane);
        anchorPane.getChildren().remove(pausedText);
    }

    public void oneStepButtonAction(ActionEvent actionEvent) {
    }

    public void restartButtonAction(ActionEvent actionEvent) throws IOException {
        initializeGame();
    }

    public void handleMove(KeyEvent keyEvent) throws IOException, InterruptedException {
        if(isPaused)
            return;
        switch (keyEvent.getCode()) {
            case Z -> {
                connectedPlayer.moveUp();
            }
            case Q -> {
                connectedPlayer.moveLeft();
            }
            case S -> {
                connectedPlayer.moveDown();
            }
            case D -> {
                connectedPlayer.moveRight();
            }
        }
        connectedPlayer.sendSurroundingRequest();
    }



    public void switchGameEnd(){
        double canvasWidth = gridCanvas.getWidth();
        double canvasHeight = gridCanvas.getHeight();
        gridCanvas.setVisible(false);
        anchorPane.getChildren().add(endPane);
        endPane.setVisible(true);
    }
    public class Leader{
        public String name;
        public int score;
        public Leader(int score, String name){
            this.name = name;
            this.score = score;
        }
    }
}

