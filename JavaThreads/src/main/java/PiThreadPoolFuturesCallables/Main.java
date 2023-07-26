package PiThreadPoolFuturesCallables;

import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    // The lock object
    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Scanner scanner = new Scanner(System.in);
        double sum = 0;

        // Get input
        System.out.print("Precision: ");
        int precision = scanner.nextInt();
        System.out.print("Number of threads: ");
        int numberOfThreads = scanner.nextInt();

        // Create thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);

        // Calculate steps
        int stepsPerThread = precision / numberOfThreads;
        int remainingSteps = precision % numberOfThreads;

        // Validation check
        if (stepsPerThread <= 0) {
            System.out.println("Invalid number of steps per thread: " + stepsPerThread);
            scanner.close();
            return;
        }

        // Create tasks
        PiCallable[] tasks = new PiCallable[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++)
            tasks[i] = new PiCallable(i, stepsPerThread);

        // Create futures
        Future<Double>[] futures = new Future[numberOfThreads];

        // Submit tasks
        for (int i = 0; i < numberOfThreads; i++)
            futures[i] = executor.submit(tasks[i]);

        // Get Futures
        for (int i = 0; i < numberOfThreads; i++)
            sum += futures[i].get();

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
                sum += factor / (2 * i + 1);

        }

        sum *= 4;
        System.out.println("Callable calculated Pi: " + sum);

        // Closing the scanner and shutting down the executor service
        scanner.close();
        executor.shutdown();

    }

}
