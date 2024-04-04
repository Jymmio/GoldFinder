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
    protected int startDelay = 2000;
    protected long startTime;
    ArrayList<RunPlayer> runPlayers = new ArrayList<>();
    ArrayList<Player> players = new ArrayList<>();
    public int gameJoinCounter = 0;
    String startMessage = "GAME_START ";
    public boolean allPlayersReady = false;

    public static void main(String[] args){
        new AppServer().start();
    }

    @Override
    public void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true){
            gameJoinCounter = 0;
            startMessage = "GAME_START ";
            players = new ArrayList<>();
            runPlayers = new ArrayList<>();
            allPlayersReady = false;
            runAGame(ss);
        }
    }
    public void runAGame(ServerSocket ss) {
        Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
        RunPlayer rp;
        int i=0;
        while (gameJoinCounter < MAX_PLAYERS) {
                Socket s = null;
                try {
                    s = ss.accept();
                    rp = new RunPlayer(s, grid, i);
                    i++;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                players.add(rp.player);
                runPlayers.add(rp);
                if(gameJoinCounter >=MAX_PLAYERS){
                    break;
                }
            }
        System.out.println("Game started correctly !");
        return;
    }
    public void setAllPlayersReady(boolean x){
        this.allPlayersReady = x;
    }
    public boolean getAllPlayersReady(){
        return this.allPlayersReady;
    }
    public void incrementJoinCounter(){
        this.gameJoinCounter++;
    }
    public int getGameJoinCounter(){
        return this.gameJoinCounter;
    }
    public String getStartMessage(){
        return this.startMessage;
    }
    public ArrayList<RunPlayer> getRunPlayers(){
        return this.runPlayers;
    }

    public class RunPlayer extends Thread {
        HashMap<String,Integer> playerLeaderScore;
        Grid grid;
        boolean isReady = false;
        private final Socket playerSocket;
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
            start();
        }
        @Override
        public void run(){
            System.out.println("PLAYER : " + player.name +", CONNECTED SUCCESSFULLY");
            String clientRequest = "";
            String requestAnswer = "";
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
                        System.out.println("let's handle this dear n°"+player.id);
                        handleStartRequests(clientRequest);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                while(true){
                    if (getRunPlayers().size() == MAX_PLAYERS) {
                        for (RunPlayer rp : getRunPlayers()) {
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
                    if(getGameJoinCounter() >=2 && getStartMessage().endsWith("END")) {
                        System.out.println("response for " + player.name + " : " + startMessage);
                        pw.println(startMessage);
                        isStartMessageSent = true;
                    }
                    System.out.print("");
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
                            playerLeaderScore.put(this.player.name,this.player.score);
                            LeaderBoardHandler lbh = new LeaderBoardHandler(playerLeaderScore);
                            lbh.gameLeaderboardBuilder();
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
        public void handleStartRequests(String msg) throws IOException {
            msg = br.readLine();
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
                incrementJoinCounter();
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

}
