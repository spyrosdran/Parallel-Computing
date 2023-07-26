package PiThreadGlobalSumReduce;

public class Thread extends java.lang.Thread {

    private final int startIndex;
    private final int finishIndex;
    private final int rank;
    private double[] sum;

    public Thread(int rank, int stepsPerThread, double[] sum) {
        startIndex = stepsPerThread * rank;
        finishIndex = startIndex + stepsPerThread;
        this.rank = rank;
        this.sum = sum;
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

        sum[rank] = localSum;

    }

}
