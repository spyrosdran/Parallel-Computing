package HelloJavaThreads;

public class Thread extends java.lang.Thread {

    private final int rank;

    public Thread(int rank) {
        this.rank = rank;
    }

    public void run() {
        System.out.println("Hello from Thread #" + rank);
    }

}
