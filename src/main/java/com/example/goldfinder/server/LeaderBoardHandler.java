package com.example.goldfinder.server;

import java.io.IOException;
import java.util.*;

public class LeaderBoardHandler {
    ArrayList<Player> players;
    HashMap<String, Integer> playersScores;

    public LeaderBoardHandler(HashMap<String, Integer> playersScores){
        this.playersScores = playersScores;
    }

    public HashMap<String, Integer> gameLeaderBoardBuilder() throws IOException {
        String[] oldPlayersScoresString = LeaderBoardFile.readFile().split("\n");
        HashMap<String, Integer> oldPlayersScores = new HashMap<>();
        if(this.playersScores != null){
            oldPlayersScores = this.playersScores;
        }

        if (oldPlayersScoresString.length > 0) {
            for (String s : oldPlayersScoresString) {
                String[] parts = s.split(":");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                oldPlayersScores.put(name, score);
            }
        }

        System.out.println("HashMap avant le tri : " + oldPlayersScores);

        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(oldPlayersScores.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue()); // Tri décroissant
            }
        });

        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
