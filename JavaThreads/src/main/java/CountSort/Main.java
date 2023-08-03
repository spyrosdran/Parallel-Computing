package CountSort;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Main {


    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        // Get user input
        System.out.print("Array size: ");
        int size = scanner.nextInt();

        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

        // Create an array with random numbers
        Random random = new Random();
        int[] numbers = new int[size];

        for(int i = 0; i < size; i++)
            numbers[i] = random.nextInt(1, 1000);

        // Calculate steps per thread and remaining steps
        int stepsPerThread = size / numberOfThreads;
        int remaining = size % numberOfThreads;

        // Declare sorted numbers' array
        int[] sortedNumbers = new int[size];

        // Create threads
        Thread[] thread = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++)
            thread[i] = new Thread( new CountSortRunnable(i, stepsPerThread, numbers, sortedNumbers, lock));

        // Start threads
        for (int i = 0; i < numberOfThreads; i++)
            thread[i].start();

        // Join threads
        for (int i = 0; i < numberOfThreads; i++)
            thread[i].join();

        // Check for any remaining characters
        if (remaining > 0) {

            int startIndex = size - remaining;
            int finishIndex = size;
            int i, j, myNum, myPlace;

            // Count sort
            for (j = startIndex; j < finishIndex; j++) {
                myNum = numbers[j];
                myPlace = 0;
                for (i = 0; i < size; i++)
                    if ((myNum > numbers[i]) || ((myNum == numbers[i]) && (j < i)))
                        myPlace++;

                sortedNumbers[myPlace] = myNum;
            }

        }

        // Print results
        for(int number: sortedNumbers)
            System.out.println(number);

        scanner.close();

    }

}

