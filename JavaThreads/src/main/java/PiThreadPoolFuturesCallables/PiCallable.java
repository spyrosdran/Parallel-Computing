package PiThreadPoolFuturesCallables;

import java.util.concurrent.Callable;

public class PiCallable implements Callable<Double> {

    private final int startIndex;
    private final int finishIndex;

    public PiCallable(int rank, int stepsPerThread) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
    }

    @Override
    public Double call() throws Exception {

        double localSum = 0;
        double factor;

        // Define the factor
        if (startIndex % 2 == 0)
            factor = 1.0;
        else
            factor = -1.0;

        // Find the local sum
        for (int i = startIndex; i < finishIndex; i++, factor = -factor)
            localSum += factor / (2 * i + 1);

        return localSum;
    }

}