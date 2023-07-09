package com.walker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    private static final String WORK_DIR = "src/main/resources/";
    private static final String SOURCE_FILE = WORK_DIR + "the-republic.txt";
    private static final String SOURCE_FILE2= WORK_DIR + "alice-in-wonderland.txt";
    private static final String OUTPUT_FOLDER = WORK_DIR + "the-republic/";
    private static final String OUTPUT_FOLDER2 = WORK_DIR + "Alice-in-wonderland/";

    public static void main(String[] args) {
        splitRepublic();
        splitAliceInWonderland();
    }

    private static void splitRepublic() {
        try {
            // Read the source file
            BufferedReader reader = new BufferedReader(new FileReader(SOURCE_FILE));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
            reader.close();

            // Create the output folder
            File folder = new File(OUTPUT_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Split the text into chapters
            List<Integer> chapterIndices = findChapterIndices(lines);
            for (int i = 0; i < chapterIndices.size(); i++) {
                int startIndex = chapterIndices.get(i);
                int endIndex = (i + 1 < chapterIndices.size()) ? chapterIndices.get(i + 1) : lines.size();
                List<String> chapterLines = lines.subList(startIndex, endIndex);

                // Extract the chapter name from the chapter line
                String chapterTitle = chapterLines.get(0).replace("BOOK", "").trim();

                // Write the chapter text to a file
                String filename = OUTPUT_FOLDER + "the-republic - " + chapterTitle + ".txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                for (String chapterLine : chapterLines) {
                    writer.write(chapterLine);
                    writer.newLine();
                }
                writer.close();
            }

            System.out.println("Splitting completed. Output files are located in: " + OUTPUT_FOLDER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> findChapterIndices(List<String> lines) {
        List<Integer> chapterIndices = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("BOOK")) {
                chapterIndices.add(i);
            }
        }
        return chapterIndices;
    }


    private static void splitAliceInWonderland() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SOURCE_FILE2))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            // Create the output folder
            File folder = new File(OUTPUT_FOLDER2);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Find the titles and indices
            List<String> chapterTitles = findChapterTitles(lines);
            List<Integer> chapterIndices = findChapterIndicesAlice(lines, chapterTitles);

            // Split the text into chapters and write to separate files
            for (int i = 0; i < chapterIndices.size(); i++) {
                int startIndex = chapterIndices.get(i);
                int endIndex = (i + 1 < chapterIndices.size()) ? chapterIndices.get(i + 1) : lines.size();
                List<String> chapterLines = lines.subList(startIndex, endIndex);

                // Extract the chapter name from the chapter title line
                String chapterTitle = chapterTitles.get(i);

                // Write the chapter text to a file
                String filename = OUTPUT_FOLDER2 + "alice-in-wonderland - " + chapterTitle + ".txt";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                    for (String chapterLine : chapterLines) {
                        writer.write(chapterLine);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Splitting completed. Output files are located in: " + OUTPUT_FOLDER2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static List<String> findChapterTitles(List<String> lines) {
        return lines.stream()
                .filter(line -> line.startsWith("CHAPTER"))
                .map(line -> line.trim())
                .collect(Collectors.toList());
    }

    private static List<Integer> findChapterIndicesAlice(List<String> lines, List<String> chapterTitles) {
      return chapterTitles.stream()
              .map(title -> lines.indexOf(title))
              .filter(index -> index !=-1)
              .collect(Collectors.toList());
    }
}
