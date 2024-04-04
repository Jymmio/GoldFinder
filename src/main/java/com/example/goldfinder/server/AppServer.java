package com.example.goldfinder.server;




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
    protected final int startDelay = 3000;
    protected long startTime;
    ArrayList<RunPlayer> runPlayers = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    public int gameJoinCounter = 0;
    String startMessage = "GAME_START ";
    private boolean isTimeStartSet = false;

    public static void main(String[] args){
        new AppServer().start();
    }

    @Override
    public void run() {
        try {
            LeaderBoardFile.writeOnFile("Hello World");
            String text = LeaderBoardFile.readFile();
            System.out.println("It wrote something : " + text);
        } catch (IOException e) {
            System.err.println("Échec de l'écriture dans le fichier : " + e.getMessage());
            e.printStackTrace();
        }
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
            System.out.println("server should be listening on port " + serverPort);
            RunPlayer rp;
            while (true) {
                Socket s = null;
                try {
                    s = ss.accept();
                    rp = new RunPlayer(s, grid, gameJoinCounter);
                    rp.start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                players.add(rp.player);
                runPlayers.add(rp);
                String msg = null;
                try {
                    msg = rp.br.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(msg.startsWith("GAME_JOIN")) {
                    System.out.println(msg);
                    rp.player.setName(msg.split(":")[1].split(" ")[0]);
                    gameJoinCounter++;
                }
                while(gameJoinCounter>=2){
                    if(!isTimeStartSet){
                        startTime = System.currentTimeMillis();
                        System.out.println("start time : " + startTime);
                        isTimeStartSet = true;
                    }
                    if(System.currentTimeMillis() - startTime >= startDelay) {
                        for (RunPlayer runp : runPlayers) {
                            startMessage+= runp.player.name + ":" + runp.player.id + " ";
                        }
                        startMessage += " END";
                        System.out.println("START MESSAGE UPDATED");
                        break;
                    }
                }
            }
        }
    public int getGameJoinCounter(){
        return this.gameJoinCounter;
    }

    public class RunPlayer extends Thread {
        Grid grid;
        private Socket playerSocket;
        protected PrintWriter pw;
        protected BufferedReader br;
        Player player;
        int discoveredWallsCounter = 0;
        public RunPlayer(Socket socket, Grid grid, int id) throws IOException {
            this.playerSocket = socket;
            this.pw = new PrintWriter(playerSocket.getOutputStream(), true);
            this.br = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            this.grid = grid;
            this.player = new Player(pw, "");
            this.player.id = id;
        }
        @Override
        public void run(){
            System.out.println("PLAYER : " + player.name +", CONNECTED SUCCESSFULLY");
            String clientRequest;
            String requestAnswer = "";
            String up = "";
            String down = "";
            String left = "";
            String right = "";
            boolean isStartMessageSent = false;
            int amountOfWalls = grid.countWalls();
            int amountOfGold = grid.goldCounter();
            int amountOfPlayers;
            while (true) {
                while(!isStartMessageSent){
                    if((amountOfPlayers = getGameJoinCounter()) >=2 && startMessage.endsWith("END")) {
                        System.out.println("response for " + player.name + " : " + startMessage);
                        pw.println(startMessage);
                        isStartMessageSent = true;
                    }
                }
                try {
                    requestAnswer = "";
                    clientRequest = br.readLine();
                    if (clientRequest.equals("PLAYER_GENERATE_POSITION")) {
                        player.generatePlayerPosition(grid);
                    }
                    if (clientRequest.equals("SURROUNDING")) {
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
                        }
                    }
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
                } catch (SocketException e) {
                    System.out.println("PLAYER DISCONNECTED !");
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
