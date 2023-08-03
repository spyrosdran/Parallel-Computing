package StringMatching;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static ReentrantLock lock = new ReentrantLock();
    public static String pattern;
    public static ArrayList<Integer> matchIndices = new ArrayList<>();
    public static IntegerObject totalMatches = new IntegerObject(0);

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        // Getting user input
        System.out.print("File's absolute path: ");
        String filePath = scanner.nextLine();

        System.out.print("Pattern to match: ");
        pattern = scanner.nextLine();

        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

        // Save file into a buffer
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);
        String buffer = String.join(System.lineSeparator(), lines);

        // Calculate steps per thread and remaining steps
        int stepsPerThread = buffer.length() / numberOfThreads;
        int remaining = buffer.length() % numberOfThreads;

        // Create threads
        Thread[] thread = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++)
            thread[i] = new Thread( new StringMatchingRunnable(i, stepsPerThread, buffer, lock, pattern, matchIndices, totalMatches));

        // Start threads
        for (int i = 0; i < numberOfThreads; i++)
            thread[i].start();

        // Join threads
        for (int i = 0; i < numberOfThreads; i++)
            thread[i].join();

        // Check for any remaining characters
        if (remaining > 0) {

            int startIndex = buffer.length() - remaining;
            int finishIndex = buffer.length();
            int i, j;

            // Brute force string matching
            for (j = startIndex; j < finishIndex; ++j) {
                for (i = 0; i < pattern.length() && pattern.charAt(i) == buffer.charAt(i + j); ++i) ;

                if (i >= pattern.length()) {
                    matchIndices.add(j);
                    totalMatches.increment();
                }
            }

        }

        // Sort match indices
        Collections.sort(matchIndices);

        // Print results
        System.out.println("---- Match indices ----");

        for(Integer num: matchIndices)
            System.out.println(num);

        System.out.println("Total matches: " + totalMatches.getValue());

        scanner.close();

    }

}

