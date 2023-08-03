package StringMatching;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class StringMatchingRunnable implements Runnable {

    private final int startIndex;
    private final int finishIndex;
    private final int stepsPerThread;
    private final int rank;
    private final String buffer;
    private final String pattern;
    private ArrayList<Integer> matchIndices;
    private IntegerObject totalMatches;
    final int N = 128;
    final int base = 0;
    private ReentrantLock lock;

    public StringMatchingRunnable(int rank, int stepsPerThread, String buffer, ReentrantLock lock, String pattern, ArrayList<Integer> matchIndices, IntegerObject totalMatches) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
        this.buffer = buffer;
        this.stepsPerThread = stepsPerThread;
        this.rank = rank;
        this.lock = lock;
        this.pattern = pattern;
        this.matchIndices = matchIndices;
        this.totalMatches = totalMatches;
    }

    @Override
    public void run() {

        int i, j;
        ArrayList<Integer> localMatchIndices = new ArrayList<>();
        Integer localTotalMatches = 0;

        // Brute force string matching
        for (j = startIndex; j < finishIndex; ++j) {
            for (i = 0; i < pattern.length() && pattern.charAt(i) == buffer.charAt(i + j); ++i) ;

            if (i >= pattern.length()) {
                localMatchIndices.add(j);
                localTotalMatches++;
            }
        }

        // Update the global variables
        lock.lock();

        totalMatches.add(localTotalMatches);

        for (Integer num: localMatchIndices)
            matchIndices.add(num);

        lock.unlock();

    }
}