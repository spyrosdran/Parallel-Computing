package HelloJavaThreads;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        int numberOfThreads = 16;

        // Declare the threads' table
        Thread[] threads = new Thread[numberOfThreads];

        // Create threads
        for(int i = 0; i < numberOfThreads; i++)
            threads[i] = new Thread(i);

        // Start threads
        for(int i = 0; i < numberOfThreads; i++)
            threads[i].start();

        // Wait for threads to finish
        for(int i = 0; i < numberOfThreads; i++)
            threads[i].join();

    }
}