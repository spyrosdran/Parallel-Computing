package PiSemaphoreGlobalSum;

import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // The lock object
    public static Semaphore sem = new Semaphore(1);
    public static SumObject globalSum = new SumObject();

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        // Get input
        System.out.print("Precision: ");
        int precision = scanner.nextInt();
        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

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
            threads[i] = new Thread(i, stepsPerThread, globalSum, sem);

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
                globalSum.add(factor / (2 * i + 1));

        }

        globalSum.sum *= 4;

        System.out.println("Thread calculated Pi: " + globalSum.sum);
        
        scanner.close();


    }

}
