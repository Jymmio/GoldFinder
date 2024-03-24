package com.example.goldfinder;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.Random;

public class GridView {
    Canvas canvas;
    int columnCount, rowCount;
    boolean[][] goldAt, vWall, hWall;


    public GridView(Canvas canvas, int columnCount, int rowCount) {
        this.canvas = canvas;
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        goldAt = new boolean[columnCount][rowCount];
        vWall = new boolean[columnCount+1][rowCount];
        hWall = new boolean[columnCount][rowCount+1];
    }

    public void repaint(){
        canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(),canvas.getHeight());
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
    public int[] paintPlayerStartPosition(){
        Random random = new Random();
        int[] results = new int[2];
        int column = random.nextInt(19)+1;
        int row = random.nextInt(19)+1;
        canvas.getGraphicsContext2D().setFill(Color.BLUE);
        canvas.getGraphicsContext2D().fillRect(column*cellWidth(),row*cellHeight(),cellWidth(),cellHeight());
        results[0] = column;
        results[1] = row;
        return results;
    }
    public void paintToken(int column, int row) {
        Random random = new Random();
        canvas.getGraphicsContext2D().setFill(Color.BLUE);
        canvas.getGraphicsContext2D().fillRect(column*cellWidth(),row*cellHeight(),cellWidth(),cellHeight());
    }
}
