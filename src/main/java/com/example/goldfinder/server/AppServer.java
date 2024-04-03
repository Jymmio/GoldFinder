package com.example.goldfinder.server;


import com.example.goldfinder.PlayerClient;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;


public class AppServer extends Thread{
    public static final int  ROW_COUNT = 20;
    public static final int COLUMN_COUNT = 20;
    final static int serverPort = 1234;
    ArrayList<RunPlayer> runPlayers = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new AppServer().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(serverPort);
            Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
            System.out.println("server should be listening on port " + serverPort);
            int gameJoinCounter = 0;
            RunPlayer rp;
            while (true) {
                Socket s = ss.accept();
                rp = new RunPlayer(s, grid);
                players.add(rp.player);
                runPlayers.add(rp);
                String msg = rp.br.readLine();
                if(msg.startsWith("GAME_JOIN")) {
                    System.out.println(msg);
                    rp.player.setName(msg.split(":")[1].split(" ")[0]);
                    gameJoinCounter++;
                }
                if(gameJoinCounter>1){
                    String startMessage = "GAME_START ";
                    for (RunPlayer runp : runPlayers){
                        startMessage+= runp.player.name;
                    }
                    for(RunPlayer runp: runPlayers){
                        runp.pw.println(startMessage);
                        runp.start();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public class RunPlayer extends Thread {
        Grid grid;
        private Socket playerSocket;
        private PrintWriter pw;
        private BufferedReader br;
        Player player;

        boolean canBroadcast = false;

        int discoveredWallsCounter = 0;
        public RunPlayer(Socket socket, Grid grid) throws IOException {
            this.playerSocket = socket;
            this.pw = new PrintWriter(playerSocket.getOutputStream(), true);
            this.br = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            this.grid = grid;
            this.player = new Player(pw, "");
        }
        @Override
        public void run(){
            System.out.println("PLAYER CONNECTED SUCCESSFULLY");
            String clientRequest;
            String requestAnswer = "";
            String up = "";
            String down = "";
            String left = "";
            String right = "";
            int amountOfWalls = grid.countWalls();
            int amountOfGold = grid.goldCounter();
            try {
                while(true){
                    requestAnswer = "";
                    clientRequest = br.readLine();
                    if(clientRequest.equals("PLAYER_GENERATE_POSITION")){
                        player.generatePlayerPosition(grid);
                    }
                    if(clientRequest.equals("SURROUNDING")){
                        if (discoveredWallsCounter == amountOfWalls && this.player.score == amountOfGold) {
                            pw.println("GAME_END " + this.player.getName() + ":" + this.player.score);
                        } else {
                        SurroundingSearcher surrssearch = new SurroundingSearcher(player.x, player.y, grid, players);
                        requestAnswer = surrssearch.searcher();
                        up = surrssearch.getUp();
                        down = surrssearch.getDown();
                        right = surrssearch.getRight();
                        left = surrssearch.getLeft();
                        if (up.equals("INVALIDMOVE")) {
                            if (!grid.ishWallVisited(player.x, player.y)) {
                                grid.sethWallVisited(player.x, player.y);
                                discoveredWallsCounter++;
                            }
                        }
                        if (down.equals("INVALIDMOVE")) {
                            if (player.y < 19) {
                                if (!grid.ishWallVisited(player.x, player.y + 1)) {
                                    grid.sethWallVisited(player.x, player.y + 1);
                                    discoveredWallsCounter++;
                                }
                            }
                        }
                        if (left.equals("INVALIDMOVE")) {
                            if (!grid.isvWallVisited(player.x, player.y)) {
                                grid.setvWallVisited(player.x, player.y);
                                discoveredWallsCounter++;
                            }
                        }
                        if (right.equals("INVALIDMOVE")) {
                            if (player.x < 19) {
                                if (!grid.isvWallVisited(player.x + 1, player.y)) {
                                    grid.setvWallVisited(player.x + 1, player.y);
                                    discoveredWallsCounter++;
                                }
                            }
                        }
                        pw.println(requestAnswer);
                    }}
                    if (clientRequest.startsWith("UP")) {
                        pw.println(moveUpRequestAnswer(up));
                    }
                    if (clientRequest.startsWith("DOWN")) {
                        pw.println(moveDownRequestAnswer(down));
                    }
                    if (clientRequest.startsWith("LEFT")) {
                        pw.println(moveLeftRequestAnswer(left));
                    }
                    if (clientRequest.startsWith("RIGHT")) {
                        pw.println(moveRightRequestAnswer(right));
                    }
                }
            } catch(SocketException e){
                System.out.println("PLAYER DISCONNECTED !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String moveDownRequestAnswer(String down){
            if (!down.equals("INVALIDMOVE") && !down.equals("P_INVALIDMOVE")) {
                player.y++;
                if (down.split(":")[1].equals("GOLD")) {
                    player.score++;
                }
            }
            grid.deleteGold(player.x, player.y);
            return down;
        }
        public String moveUpRequestAnswer(String up){
            if (!up.equals("INVALIDMOVE") && !up.equals("P_INVALIDMOVE")) {
                player.y--;
                if (up.split(":")[1].equals("GOLD")) {
                    player.score++;
                }
            }
            grid.deleteGold(player.x, player.y);
            return up;
        }
        public String moveLeftRequestAnswer(String left){
            if (!left.equals("INVALIDMOVE") && !left.equals("P_INVALIDMOVE")) {
                player.x--;
                if (left.split(":")[1].equals("GOLD")) {
                    player.score++;
                }
            }
            grid.deleteGold(player.x, player.y);
            return left;
        }
        public String moveRightRequestAnswer(String right){
            if (!right.equals("INVALIDMOVE") && !right.equals("P_INVALIDMOVE")) {
                player.x++;
                if (right.split(":")[1].equals("GOLD")) {
                    player.score++;
                }
            }
            grid.deleteGold(player.x, player.y);
            return right;
        }
    }

}
