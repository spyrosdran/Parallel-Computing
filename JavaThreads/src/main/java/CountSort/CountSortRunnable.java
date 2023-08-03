package CountSort;

import java.util.concurrent.locks.ReentrantLock;

public class CountSortRunnable implements Runnable {

    private final int startIndex;
    private final int finishIndex;
    private final int stepsPerThread;
    private final int rank;
    private final int[] numbers;
    private int[] sortedNumbers;

    private ReentrantLock lock;

    public CountSortRunnable(int rank, int stepsPerThread, int[] numbers, int[] sortedNumbers, ReentrantLock lock) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
        this.stepsPerThread = stepsPerThread;
        this.rank = rank;
        this.numbers = numbers;
        this.sortedNumbers = sortedNumbers;
        this.lock = lock;
    }

    @Override
    public void run() {

        // Declare local variables
        int i, j, myNum, myPlace, counter = 0;
        int[] values = new int[stepsPerThread];
        int[] positions = new int[stepsPerThread];

        // Count sort
        for (j = startIndex; j < finishIndex; j++)
        {
            myNum = numbers[j];
            myPlace = 0;

            for (i = 0; i < numbers.length; i++)
                if ((myNum > numbers[i]) || ((myNum == numbers[i]) && (j < i)))
                    myPlace++;

            positions[counter] = myPlace;
            values[counter] = myNum;
            counter++;
        }

        // Update the global sorted numbers
        lock.lock();

        for (i = 0; i < counter; i++)
            sortedNumbers[positions[i]] = values[i];

        lock.unlock();

    }
}