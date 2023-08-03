package CharacterFrequency;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static final int N = 128;
    public static int[] globalFrequencies = new int[N];
    public static ReentrantLock lock = new ReentrantLock();
    public static final int base = 0;

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("File's absolute path: ");
        String filePath = scanner.nextLine();

        // Save file into a buffer
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);
        String buffer = String.join(System.lineSeparator(), lines);

        // Read the number of threads
        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

        // Calculate steps per thread and remaining steps
        int stepsPerThread = buffer.length() / numberOfThreads;
        int remaining = buffer.length() % numberOfThreads;

        // Initialize global frequencies table
        for (int i = 0; i < N; i++)
            globalFrequencies[i] = 0;

        // Create threads
        Thread[] thread = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++)
            thread[i] = new Thread( new CharRunnable(i, stepsPerThread, buffer, globalFrequencies, lock));

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

            for (int i = startIndex; i < finishIndex; i++)
                globalFrequencies[ (int) (buffer.charAt(i)) - base]++;
        }

        // Print results
        for(int i = 0; i < N; i++)
            System.out.println(i + " = " + globalFrequencies[i]);

        scanner.close();

    }

}

