package com.example.goldfinder;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class GridView {
    Canvas canvas;
    int columnCount, rowCount;
    public boolean[][] goldAt;
    public boolean[][] vWall;
    public boolean[][] hWall;


    public GridView(Canvas canvas, int columnCount, int rowCount) {
        this.canvas = canvas;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        goldAt = new boolean[columnCount][rowCount];
        vWall = new boolean[columnCount+1][rowCount];
        hWall = new boolean[columnCount][rowCount+1];
    }

    public void repaint(int playerColumn, int playerRow, ArrayList<ArrayList<Integer>> allPlayers){
        canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        paintToken(playerColumn, playerRow);
        if(allPlayers != null){
            for(ArrayList<Integer> playerPos : allPlayers){
                System.out.println("voisin_col : " + playerPos.get(0) + " voisin_row : " + playerPos.get(1));
                paintToken2(playerPos.get(0),playerPos.get(1));
            }
        }
        for(int column =0; column<columnCount;column++)
            for(int row=0;row<rowCount;row++)
                if(goldAt[column][row]) {
                    canvas.getGraphicsContext2D().setFill(Color.YELLOW);
                    canvas.getGraphicsContext2D().fillOval(column * cellWidth(), row * cellHeight(), cellWidth(), cellHeight());
                }
        canvas.getGraphicsContext2D().setStroke(Color.WHITE);
        for(int column =0; column<columnCount;column++)
            for(int row=0;row<rowCount;row++){
                if(vWall[column][row])
                    canvas.getGraphicsContext2D().strokeLine(column * cellWidth(), row * cellHeight(),column * cellWidth(), (row+1) * cellHeight());
                if(hWall[column][row])
                    canvas.getGraphicsContext2D().strokeLine(column * cellWidth(), row * cellHeight(),(column+1) * cellWidth(), row * cellHeight());
            }

    }

    private double cellWidth(){ return canvas.getWidth()/columnCount; }
    private double cellHeight(){ return canvas.getHeight()/rowCount; }
    public void paintToken(int column, int row) {
        canvas.getGraphicsContext2D().setFill(Color.BLUE);
        canvas.getGraphicsContext2D().fillRect(column*cellWidth()+2.5,row*cellHeight()+2.5,cellWidth()-5,cellHeight()-5);
    }
    public void paintToken2(int column, int row) {
        canvas.getGraphicsContext2D().setFill(Color.RED);
        canvas.getGraphicsContext2D().fillRect(column*cellWidth()+2.5,row*cellHeight()+2.5,cellWidth()-5,cellHeight()-5);
    }
}