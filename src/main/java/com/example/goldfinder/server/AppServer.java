package com.example.goldfinder.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class AppServer extends Thread{
    public static final int  ROW_COUNT = 20;
    public static final int COLUMN_COUNT = 20;
    final static int serverPort = 1234;

    public static void main(String[] args) throws IOException {
        new AppServer().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(serverPort);
            while (true) {
                System.out.println("server should be listening on port " + serverPort);
                Socket s = ss.accept();
                Player player = new Player(s);
                player.generatePlayerStartPosition();

                Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendPlayerStartPosition(Socket playerSocket){
        Player player = new Player(playerSocket);

    }
    public class Player{
        int x;
        int y;
        private Socket playerSocket;
        public Player(Socket playerSocket){
            this.playerSocket = playerSocket;
        }

        public void generatePlayerStartPosition() throws IOException {
            Random random = new Random();
            int column = random.nextInt(19)+1;
            int row = random.nextInt(19)+1;
            OutputStream os = playerSocket.getOutputStream();
            os.write(column);
            os.write(row);
            System.out.println("s.row : " + row + " | s.col : " + column);
        }
    }

}
