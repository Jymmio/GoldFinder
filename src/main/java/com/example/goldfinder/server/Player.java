package com.example.goldfinder.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Player{
    int x;
    int y;
    String name;
    int id;
    public int score;
    private final PrintWriter pw;
    public Player(PrintWriter pw, String name){
        this.pw = pw;
        this.name = name;
        this.score = 0;
    }
    public String getName(){
        return this.name;
    }
    public int getRow(){return this.y;}
    public int getColumn(){return this.x;}
    public void setName(String name){
        this.name = name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void generatePlayerPosition(Grid grid) throws IOException {
        Random random = new Random();
        do{
            int column = random.nextInt(19) + 1;
            pw.println(String.valueOf(column));
            this.x = column;
            int row = random.nextInt(19) + 1;
            pw.println(String.valueOf(row));
            this.y = row;
        }while(grid.hasGold(this.x,this.y));
    }
}