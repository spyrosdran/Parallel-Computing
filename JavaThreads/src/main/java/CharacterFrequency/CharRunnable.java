package CharacterFrequency;

import java.util.concurrent.locks.ReentrantLock;

public class CharRunnable implements Runnable {

    private final int startIndex;
    private final int finishIndex;
    private final int stepsPerThread;
    private final int rank;
    private final String buffer;
    final int N = 128;
    final int base = 0;
    private int[] globalFrequencies;
    private ReentrantLock lock;

    public CharRunnable(int rank, int stepsPerThread, String buffer, int[] globalFrequencies, ReentrantLock lock) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
        this.buffer = buffer;
        this.stepsPerThread = stepsPerThread;
        this.rank = rank;
        this.globalFrequencies = globalFrequencies;
        this.lock = lock;
    }

    @Override
    public void run() {

        // Declare and initialize the local frequency table
        int[] localFrequencies = new int[N];

        for(int i = 0; i < N; i++)
            localFrequencies[i] = 0;

        // Calculate the local frequency table
        for (int i = startIndex; i < finishIndex; i++)
            localFrequencies[ (int) (buffer.charAt(i)) - base]++;

        // Update the global frequencies
        lock.lock();

        for(int i = 0; i < N; i++)
            globalFrequencies[i] += localFrequencies[i];

        lock.unlock();

    }
}