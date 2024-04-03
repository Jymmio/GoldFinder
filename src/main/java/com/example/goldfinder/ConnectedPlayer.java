package com.example.goldfinder;

import com.example.goldfinder.Controller;
import com.example.goldfinder.GridView;
import com.example.goldfinder.PlayerClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ConnectedPlayer {
    Controller controller;
    public PlayerClient player;
    BufferedReader br;
    PrintWriter pw;
    String upSurrounding,downSurrounding,rightSurrounding,leftSurrounding;

    public ConnectedPlayer(PrintWriter pw, BufferedReader br, Controller c){
        this.br = br;
        this.pw = pw;
        this.controller = c;
    }
    public void setPlayer(String name) throws IOException {
        this.player = new PlayerClient(name,this.pw, this.br);
    }
    public void moveUp() throws IOException {
        sendSurroundingRequest();
        pw.println("UP");
        upSurrounding = br.readLine();
        if(!upSurrounding.equals("INVALIDMOVE") && !upSurrounding.equals("P_INVALIDMOVE")){
            player.y--;
            if(upSurrounding.split(":")[1].equals("GOLD")) {
                controller.gridView.goldAt[player.x][player.y] = false;
                player.scoreIncrement();
            }
        }
    }
    public void moveLeft() throws IOException{
        sendSurroundingRequest();
        pw.println("LEFT");
        leftSurrounding = br.readLine();
        if(!leftSurrounding.equals("INVALIDMOVE") && !leftSurrounding.equals("P_INVALIDMOVE")){
            player.x--;
            if(leftSurrounding.split(":")[1].equals("GOLD")) {
                controller.gridView.goldAt[player.x][player.y] = false;
                player.scoreIncrement();
            }
        }
    }
    public void moveDown() throws IOException {
        sendSurroundingRequest();
        pw.println("DOWN");
        downSurrounding = br.readLine();
        if(!downSurrounding.equals("INVALIDMOVE") && !downSurrounding.equals("P_INVALIDMOVE")){
            player.y++;
            if(downSurrounding.split(":")[1].equals("GOLD")) {
                controller.gridView.goldAt[player.x][player.y] = false;
                player.scoreIncrement();
            }
        }
    }
    public void moveRight() throws IOException {
        sendSurroundingRequest();
        pw.println("RIGHT");
        rightSurrounding = br.readLine();
        if(!rightSurrounding.equals("INVALIDMOVE") && !rightSurrounding.equals("P_INVALIDMOVE")){
            player.x++;
            if(rightSurrounding.split(":")[1].equals("GOLD")) {
                controller.gridView.goldAt[player.x][player.y] = false;
                player.scoreIncrement();
            }
        }
    }
    public void sendSurroundingRequest() throws IOException{
        pw.println("SURROUNDING");
        String surrounding = br.readLine();
        System.out.println(surrounding);
        if(surrounding.startsWith("GAME_END")){
            controller.switchGameEnd();
        }
        else{
            setSurrParts(surrounding);
            drawSurrounding(upSurrounding, downSurrounding, leftSurrounding, rightSurrounding);
        }
    }
    public void setSurrParts(String surrounding){
        String[] parts = surrounding.split(" ");

        // Parcourir les parties et extraire les éléments individuels
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                // Stocker les éléments individuels dans les variables appropriées
                switch (key) {
                    case "UP":
                        upSurrounding = value;
                        break;
                    case "RIGHT":
                        rightSurrounding = value;
                        break;
                    case "DOWN":
                        downSurrounding = value;
                        break;
                    case "LEFT":
                        leftSurrounding = value;
                        break;
                }
            }
        }
    }
    public void drawSurrounding(String up, String down, String left, String right){
        ArrayList<ArrayList<Integer>> playersPositions = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> onePlayerPosition = new ArrayList<>();
        switch (up){
            case "WALL" -> {
                controller.gridView.hWall[player.x][player.y] = true;
            }
            case "GOLD" -> {
                controller.gridView.goldAt[player.x][player.y-1] = true;
            }
            default -> {
                if(up.startsWith("PLAYER")){
                    onePlayerPosition.add(player.x);
                    onePlayerPosition.add(player.y-1);
                    playersPositions.add(onePlayerPosition);
                }
            }
        }
        switch(down){
            case "WALL" -> {
                controller.gridView.hWall[player.x][player.y+1] = true;
            }
            case "GOLD" -> {
                controller.gridView.goldAt[player.x][player.y+1] = true;
            }

            default -> {
                if(down.startsWith("PLAYER")){
                    onePlayerPosition = new ArrayList<>();
                    onePlayerPosition.add(player.x);
                    onePlayerPosition.add(player.y+1);
                    playersPositions.add(onePlayerPosition);
                }
            }
        }
        switch(left){
            case "WALL" -> {
                controller.gridView.vWall[player.x][player.y] = true;
            }
            case "GOLD" -> {
                controller.gridView.goldAt[player.x-1][player.y] = true;
            }

            default -> {
                if(left.startsWith("PLAYER")){
                    onePlayerPosition = new ArrayList<>();
                    onePlayerPosition.add(player.x-1);
                    onePlayerPosition.add(player.y);
                    playersPositions.add(onePlayerPosition);
                }
            }
        }
        switch(right){
            case "WALL" -> {
                controller.gridView.vWall[player.x+1][player.y] = true;
            }
            case "GOLD" -> {
                controller.gridView.goldAt[player.x+1][player.y] = true;
            }
            default -> {
                if(right.startsWith("PLAYER")){
                    onePlayerPosition = new ArrayList<>();
                    onePlayerPosition.add(player.x+1);
                    onePlayerPosition.add(player.y);
                    playersPositions.add(onePlayerPosition);
                }
            }
        }
        controller.gridView.repaint(player.x,player.y, playersPositions);
    }

}
