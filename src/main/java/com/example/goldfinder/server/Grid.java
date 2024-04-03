package com.example.goldfinder.server;

import java.util.Random;

public class Grid {
    boolean[][] isvWallVisited = new boolean[20][20];
    boolean[][] ishWallVisited = new boolean[20][20];
    boolean[][] hWall, vWall, gold;
    int columnCount, rowCount;

    private final Random random;
    public Grid(int columnCount, int rowCount, Random random) {
        this.columnCount = columnCount;
        this.rowCount = rowCount;
        this.random = random;

        RandomMaze randomMaze = new RandomMaze(columnCount,rowCount,.1, random);
        randomMaze.generate();
        hWall = randomMaze.hWall;
        vWall = randomMaze.vWall;

        gold = new boolean [columnCount][rowCount];
        generateGold(15);
    }

    private void generateGold(double v) {
        for(int column=0; column<columnCount; column++)
            for(int row=0;row<rowCount; row++)
                gold[column][row]=(random.nextInt(150)<v);
    }

    boolean leftWall(int column, int row){
            if (column == 0) return true;
            return vWall[column][row];
    }

    boolean rightWall(int column, int row){
            if (column == columnCount - 1) return true;
            return vWall[column + 1][row];

    }

    boolean upWall(int column, int row){
            if (row == 0) return true;
            return hWall[column][row];
    }

    boolean downWall(int column, int row){
            if (row == rowCount - 1) return true;
            return hWall[column][row + 1];
    }

    boolean hasGold(int column, int row){
        return gold[column][row];
    }
    void deleteGold(int column, int row) {
        if(this.hasGold(column,row))
            gold[column][row] = false;
    }
    public int goldCounter(){
        int goldCount = 0;
        for(int i=0; i<rowCount; i++){
            for(int j=0; j<columnCount; j++){
                if(hasGold(j,i))
                    goldCount++;
            }
        }
        return goldCount;
    }
    public int countWalls(){
        int wallCounter = 0;
        for (int i=0;i<columnCount;i++){
            for(int j=0; j<rowCount; j++){
                if(hWall[i][j])
                    wallCounter++;
                if(vWall[i][j])
                    wallCounter++;
            }
        }
        return wallCounter+4;
    }
    public boolean isvWallVisited(int column, int row){
        return isvWallVisited[column][row];
    }
    public void setvWallVisited(int column, int row){
        if(!isvWallVisited[column][row])
            isvWallVisited[column][row] = true;
    }
    public boolean ishWallVisited(int column, int row){
        return ishWallVisited[column][row];
    }
    public void sethWallVisited(int column, int row){
        if(!ishWallVisited[column][row])
            ishWallVisited[column][row] = true;
    }
    public void initIsWallVisited(){
        for(boolean[] visited : isvWallVisited){
            for(boolean v : visited){
                v = false;
            }
        }
        for(boolean[] visited : ishWallVisited){
            for(boolean v : visited){
                v = false;
            }
        }
    }
}