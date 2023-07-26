package PiThreadGlobalSumReduce;

import java.util.Scanner;

public class Main {

    // Global sum table
    public static double[] sum;

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        double localSum = 0;

        // Get input
        System.out.print("Precision: ");
        int precision = scanner.nextInt();
        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

        sum = new double[numberOfThreads];

        // Calculate steps
        int stepsPerThread = precision / numberOfThreads;
        int remainingSteps = precision % numberOfThreads;

        // Validation check
        if (stepsPerThread <= 0) {
            System.out.println("Invalid number of steps per thread: " + stepsPerThread);
            scanner.close();
            return;
        }

        // Create threads
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++)
            threads[i] = new Thread(i, stepsPerThread, sum);

        // Start threads
        for (int i = 0; i < numberOfThreads; i++)
            threads[i].start();

        // Wait for threads to finish
        for (int i = 0; i < numberOfThreads; i++)
            threads[i].join();

        // Check for any remaining steps
        if (remainingSteps > 0) {

            int startIndex = precision - remainingSteps;
            int finishIndex = precision;
            int factor;

            if (startIndex % 2 == 0)
                factor = 1;
            else
                factor = -1;

            for (int i = startIndex; i < finishIndex; i++, factor = -factor)
                localSum += factor / (2 * i + 1);

        }

        // Reduction of sum
        double totalSum = 0;

        for(int i = 0; i < numberOfThreads; i++)
            totalSum += sum[i];

        totalSum += localSum;
        totalSum *= 4;

        System.out.println("Thread calculated Pi: " + totalSum);
        
        scanner.close();


    }

}
