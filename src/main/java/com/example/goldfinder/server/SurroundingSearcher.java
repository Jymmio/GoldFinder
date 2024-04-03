package com.example.goldfinder.server;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SurroundingSearcher {
    ArrayList<Player> players = new ArrayList<>();
    String result;
    int playerX;
    int playerY;
    Grid grid;
    String up,down,left,right;

    public SurroundingSearcher(int playerX, int playerY, Grid grid, ArrayList<Player> players){
        this.playerX = playerX;
        this.playerY = playerY;
        this.grid = grid;
        this.players = players;
    }
    public void upSearcher() {
        try{
            for(Player player : this.players){
                if(this.playerY-1 == player.y && this.playerX == player.x){
                    result = "UP:PLAYER"+player.getId() + " ";
                    up = "P_INVALIDMOVE";
                    return;
                }
            }
            if (grid.upWall(playerX, playerY)) {
                result = "UP:WALL ";
                up = "INVALIDMOVE";
            }
            else if (grid.hasGold(playerX, playerY - 1)) {
                result = "UP:GOLD ";
                up = "VALIDMOVE:GOLD";
            }
            else {
                result = "UP:EMPTY ";
                up = "VALIDMOVE:EMPTY";
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            result = "UP:EXCEPTION ";
        }
    }
    public void rightSearcher(){
        try{
            for(Player player : this.players){
                if(this.playerY == player.y && this.playerX+1 == player.x){
                    result += "RIGHT:PLAYER"+player.getId() + " ";
                    right = "P_INVALIDMOVE";
                    return;
                }
            }
            if (grid.rightWall(playerX, playerY)) {
                result += "RIGHT:WALL ";
                right = "INVALIDMOVE";
            }
            else if (grid.hasGold(playerX + 1, playerY)) {
                result += "RIGHT:GOLD ";
                right = "VALIDMOVE:GOLD";
            }
            else {
                result += "RIGHT:EMPTY ";
                right = "VALIDMOVE:EMPTY";
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            result += "RIGHT:EXCEPTION ";
        }
    }
    public void downSearcher(){
        try{
            for(Player player : this.players){
                if(this.playerY+1 == player.y && this.playerX == player.x){
                    result += "DOWN:PLAYER"+player.getId() + " ";
                    down = "P_INVALIDMOVE";
                    return;
                }
            }
            if (grid.downWall(playerX, playerY)) {
                result += "DOWN:WALL ";
                down = "INVALIDMOVE";
            }
            else if (grid.hasGold(playerX, playerY + 1)) {
                result += "DOWN:GOLD ";
                down = "VALIDMOVE:GOLD";
            }
            else {
                result += "DOWN:EMPTY ";
                down = "VALIDMOVE:EMPTY";
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            result += "DOWN:EXCEPTION ";
        }
    }
    public void leftSearcher(){
        try{
            for(Player player : this.players){
                if(this.playerX-1 == player.x && this.playerY == player.y){
                    result += "LEFT:PLAYER"+player.getId() + " ";
                    left = "P_INVALIDMOVE";
                    return;
                }
            }
            if (grid.leftWall(playerX, playerY)) {
                result += "LEFT:WALL ";
                left = "INVALIDMOVE";
            }
            else if (grid.hasGold(playerX - 1, playerY)) {
                result += "LEFT:GOLD ";
                left = "VALIDMOVE:GOLD";
            }
            else {
                result += "LEFT:EMPTY ";
                left = "VALIDMOVE:EMPTY";
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            result += "LEFT:EXCEPTION ";
        }
    }
    public String searcher(){
        upSearcher();
        rightSearcher();
        downSearcher();
        leftSearcher();
        result+="END";
        return result;
    }
    public String getUp(){
        return this.up;
    }
    public String getDown(){
        return this.down;
    }
    public String getLeft(){
        return this.left;
    }
    public String getRight(){
        return this.right;
    }
}
