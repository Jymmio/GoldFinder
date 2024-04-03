package com.example.goldfinder;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BotPlayer {

    ConnectedPlayer connectedPlayer;
    public BotPlayer(ConnectedPlayer cp){
        this.connectedPlayer = cp;
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Random random = new Random();
                int randomDirection = random.nextInt(4);
                try {
                    switch (randomDirection) {
                        case 0 -> connectedPlayer.moveUp();
                        case 1 -> connectedPlayer.moveDown();
                        case 2 -> connectedPlayer.moveLeft();
                        case 3 -> connectedPlayer.moveRight();
                        default -> {
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 0, 50);
    }
}
