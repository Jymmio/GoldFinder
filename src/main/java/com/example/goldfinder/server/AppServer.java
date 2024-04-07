package com.example.goldfinder.server;




import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;


public class AppServer extends Thread{
    private final int MAX_PLAYERS = 2;
    public static final int  ROW_COUNT = 20;
    public static final int COLUMN_COUNT = 20;
    final static int serverPort = 1234;
    ArrayList<ArrayList<RunPlayer>> allGamesRunPlayers = new ArrayList<>();
    public volatile ArrayList<ArrayList<Player>> players = new ArrayList<>();
    public ArrayList<Integer> gameJoinCounter = new ArrayList<>();
    String startMessage = "GAME_START ";
    public boolean allPlayersReady = false;
    public boolean isGameReadyToStart = true;
    public volatile boolean started = false;

    public static void main(String[] args){
        new AppServer().start();
    }

    @Override
    public void run() {
            ServerSocket ss;
            try {
                ss = new ServerSocket(serverPort);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int i=1;
            gameJoinCounter.add(0);
            players.add(new ArrayList<Player>());
            System.out.println("Game n°" + i);
            startMessage = "GAME_START ";
            allGamesRunPlayers.add(new ArrayList<>());
            allPlayersReady = false;
            RunAGame runAGame = new RunAGame(ss, i - 1);
            i++;
            while (true) {
                if(started){
                    started = false;
                    gameJoinCounter.add(0);
                    players.add(new ArrayList<Player>());
                    System.out.println("Game n°" + i);
                    startMessage = "GAME_START ";
                    allGamesRunPlayers.add(new ArrayList<>());
                    allPlayersReady = false;
                    runAGame = new RunAGame(ss, i - 1);
                    i++;
                }
            }
    }

    public class RunAGame extends Thread {
        ServerSocket ss;
        int gameId;
        public RunAGame(ServerSocket ss, int gameId){
            this.ss = ss;
            this.gameId = gameId;
            this.start();
        }
        @Override
        public void run() {
            Socket s;
            Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
            RunPlayer rp;
            int i = 0;
            while (gameJoinCounter.get(this.gameId) < MAX_PLAYERS) {
                try {
                    s = ss.accept();
                    incrementJoinCounter(this.gameId);
                    System.out.println(getGameJoinCounter(this.gameId));
                    rp = new RunPlayer(s, grid, i,this.gameId);
                    i++;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                players.get(this.gameId).add(rp.player);
                allGamesRunPlayers.get(this.gameId).add(rp);
            }
            started = true;
            System.out.println("Game started correctly !");
        }
    }

    public class RunPlayer extends Thread {
        HashMap<String,Integer> playerLeaderScore;
        Grid grid;
        int gameId;
        boolean isReady = false;
        protected PrintWriter pw;
        protected BufferedReader br;
        Player player;
        int discoveredWallsCounter = 0;
        public RunPlayer(Socket socket, Grid grid, int id, int gameId) throws IOException {
            this.pw = new PrintWriter(socket.getOutputStream(), true);
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.grid = grid;
            this.player = new Player(pw, "");
            this.player.id = id;
            this.gameId = gameId;
            start();
        }
        @Override
        public void run(){
            System.out.println("PLAYER : " + player.name +", CONNECTED SUCCESSFULLY");
            String clientRequest;
            String requestAnswer;
            String up = "";
            String down = "";
            String left = "";
            String right = "";
            boolean isStartMessageSent = false;
            int amountOfWalls = grid.countWalls();
            int amountOfGold = grid.goldCounter();
            while (true) {
                while(!isReady){
                    try {
                        handleStartRequests();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                while(!getAllPlayersReady()){
                    if (getRunPlayers(this.gameId).size() == MAX_PLAYERS) {
                        for (RunPlayer rp : getRunPlayers(this.gameId)) {
                            if (!isReady) {
                                setAllPlayersReady(false);
                                break;
                            }
                            startMessage += rp.player.name + ":" + rp.player.id + " ";
                            setAllPlayersReady(true);
                        }
                    }
                    if(getAllPlayersReady()) {
                        startMessage += "END";
                        break;
                    }
                }
                while(!isStartMessageSent){
                    if(getGameJoinCounter(this.gameId) >=MAX_PLAYERS && getStartMessage().endsWith("END")) {
                        System.out.println("response for " + player.name + " : " + startMessage);
                        pw.println(startMessage);
                        isStartMessageSent = true;
                    }
                    System.out.print("");
                }
                try {
                    clientRequest = br.readLine();
                    if (clientRequest.equals("PLAYER_GENERATE_POSITION")) {
                        player.generatePlayerPosition(grid);
                    }
                    if (clientRequest.equals("SURROUNDING")) {
                        if (discoveredWallsCounter == amountOfWalls && this.player.score == amountOfGold) {
                            LeaderBoardFile.writeOnFile(this.player.name+":"+this.player.score);
                            pw.println("GAME_END " + this.player.getName() + ":" + this.player.score);
                        } else {
                            SurroundingSearcher surrssearch = new SurroundingSearcher(player.x, player.y, grid, players.get(this.gameId));
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
                        /*player.score = amountOfGold;
                        discoveredWallsCounter = amountOfWalls;*/ // cheat code to finish the game quickly^^"
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
        public void handleStartRequests() throws IOException {
            String msg = br.readLine();
            if (msg.startsWith("LEADER")) {
                HashMap<String, Integer> hash;
                LeaderBoardHandler lbh = new LeaderBoardHandler(playerLeaderScore);
                try {
                    hash = lbh.gameLeaderboardBuilder();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (hash.size() > 0) {
                    for (Map.Entry<String, Integer> entry : hash.entrySet()) {
                        pw.println("SCORE:" + entry.getKey() + ":" + entry.getValue());
                    }
                    pw.println("END");
                } else {
                    pw.println("NO_LEADERS");
                }
            }
            if (msg.startsWith("GAME_JOIN")) {
                System.out.println(msg);
                player.setName(msg.split(":")[1].split(" ")[0]);
                isReady = true;
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

    public void setAllPlayersReady(boolean x){
        this.allPlayersReady = x;
    }
    public boolean getAllPlayersReady(){
        return this.allPlayersReady;
    }
    public void incrementJoinCounter(int gameId){
        int x = gameJoinCounter.get(gameId) + 1;
        gameJoinCounter.set(gameId,x);
    }
    public int getGameJoinCounter(int gameId){
        return gameJoinCounter.get(gameId);
    }
    public String getStartMessage(){
        return this.startMessage;
    }
    public void setIsGameReadyToStart(boolean b){
        isGameReadyToStart = b;
    }
    public boolean getIsGameReadyToStart(){
        return isGameReadyToStart;
    }
    public ArrayList<RunPlayer> getRunPlayers(int gameId){
        return allGamesRunPlayers.get(gameId);
    }

}
