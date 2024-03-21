package com.example.goldfinder.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class AppServer {

    public static final int  ROW_COUNT = 20;
    public static final int COLUMN_COUNT = 20;
    final static int serverPort = 1234;

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(serverPort);
        Socket s = ss.accept();
        InputStream is = s.getInputStream();
        OutputStream os = s.getOutputStream();

        Grid grid = new Grid(COLUMN_COUNT, ROW_COUNT, new Random());
        System.out.println("server should be listening on port " + serverPort);
    }

}
