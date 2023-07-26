package PiLockThreadGlobalSum;

import java.util.concurrent.locks.ReentrantLock;

public class Thread extends java.lang.Thread {

    private final int startIndex;
    private final int finishIndex;
    private SumObject globalSum;
    private ReentrantLock lock;

    public Thread(int rank, int stepsPerThread, SumObject globalSum, ReentrantLock lock) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
        this.globalSum = globalSum;
        this.lock = lock;
    }

    public void run() {

        double localSum = 0;
        double factor;

        // Define the factor
        if (startIndex % 2 == 0)
            factor = 1.0;
        else
            factor = -1.0;

        // Find the local sum
        for(int i = startIndex; i < finishIndex; i++, factor = -factor)
            localSum += factor / (2 * i + 1);

        // Acquire the lock
        lock.lock();

        // Add local sum to the global sum
        globalSum.add(localSum);

        // Release the lock
        lock.unlock();

    }

}
