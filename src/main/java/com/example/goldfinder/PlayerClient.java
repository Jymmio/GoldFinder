package com.example.goldfinder;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerClient {
    PrintWriter pw;
    BufferedReader br;
    public int x;
    public int y;
    String name;
    private final IntegerProperty scoreProperty = new SimpleIntegerProperty();

    public PlayerClient(String name, PrintWriter pw, BufferedReader br) throws IOException {
            this.name = name;
            this.pw = pw;
            this.br = br;
            this.generatePlayerPosition();
            scoreProperty.setValue(0);
    }
    public IntegerProperty scoreProperty() {
        return scoreProperty;
    }
    public String getName(){
        return this.name;
    }
    public int getRow(){
        return this.y;
    }
    public int getColumn(){
        return this.x;
    }
    public void scoreIncrement(){
        scoreProperty.setValue(scoreProperty.getValue()+1);
    }
    public void generatePlayerPosition() throws IOException {
        pw.println("PLAYER_GENERATE_POSITION");
        System.out.println("sent position request");
        String colString = br.readLine();
        System.out.println("col reçu : " + colString);
        this.x = Integer.parseInt(colString);
        String rowString = br.readLine();
        System.out.println("row reçu : " + rowString);
        this.y = Integer.parseInt(rowString);

    }
    public void incrementColumn(){
        this.x++;
    }
    public void decrementColumn(){
        this.x--;
    }
    public void incrementRow(){
        this.y++;
    }
    public void decrementRow(){
        this.y--;
    }
}
