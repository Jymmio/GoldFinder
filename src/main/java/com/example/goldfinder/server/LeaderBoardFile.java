package com.example.goldfinder.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class LeaderBoardFile {
    private static final String SOURCE_FILE_PATH = "src/main/java/com/example/goldfinder/server/leaderboard.txt";
    public static String readFile() throws IOException {
        StringBuilder contenu = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(SOURCE_FILE_PATH)))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                contenu.append(ligne).append("\n");
            }
        }
        return contenu.toString();
    }

    public static void writeOnFile(String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SOURCE_FILE_PATH))) {
            bw.write(text);
        }
    }
}
