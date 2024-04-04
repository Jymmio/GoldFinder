package com.example.goldfinder;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;

public class AppClient extends javafx.application.Application {
    private static final String START_RESOURCE_PATH = "/com/example/goldfinder/gridView.fxml";
    private static final String APP_NAME = "Gold Finder";
    private final Socket socket = new Socket("localhost", 1234);
    private final BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    private final PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

    private Stage primaryStage;
    private Parent view;

    public AppClient() throws IOException {
    }

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
        ConnectedPlayer connectedPlayer = new ConnectedPlayer(pw,br,controller);
        controller.setConnectedPlayer(connectedPlayer);
        view.setOnKeyPressed(event -> {
            try {
                controller.handleMove(event);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });   //Removed for task 3.
        controller.initializeGame();
        controller.play.setOnAction((event) -> {
            try{
                String name = (controller.textField.getText());
                System.out.println(name);
                pw.println("GAME_JOIN:" + name + " END");

                String res = br.readLine();
                System.out.println("SERVER RESPONSE TO GAME_JOIN : ");
                if (res.startsWith("GAME_START")) {
                    controller.setPlayer(name);
                    controller.startGame();
                }
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        controller.leaderboardButton.setOnAction((event) -> {
            pw.println("LEADER:5");
            String[] leaders = new String[5];
            try {
                leaders[0] = br.readLine();
                if(leaders[0].startsWith("NO")){
                    controller.showNoLeader();
                }
                else{
                    for(int i=1; i<leaders.length;i++){
                        leaders[i] = br.readLine();
                        if(leaders[i].equals("END"))
                            break;
                    }
                    controller.showLeaders(leaders);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        controller.leaderboardButton.setOnMouseEntered(event -> {
            controller.leaderboardButton.setStyle("-fx-background-color:  rgba(200,200,200,1)");
        });
        controller.play.setOnMouseEntered(event -> {
            controller.play.setStyle("-fx-background-color:  rgba(200,200,200,1)");
        });
        controller.leaderboardButton.setOnMouseExited(event -> {
            controller.leaderboardButton.setStyle("-fx-background-color:  rgba(150,150,150,0.5)");
        });
        controller.play.setOnMouseExited(event -> {
            controller.play.setStyle("-fx-background-color:  rgba(150,150,150,0.5)");
        });
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Fermeture de la fenetre ...");
            controller.shutDownBot();
        });
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
