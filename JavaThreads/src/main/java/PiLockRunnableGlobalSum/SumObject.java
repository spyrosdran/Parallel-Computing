package PiLockRunnableGlobalSum;

public class SumObject {
    public double sum;

    public SumObject() {
        sum = 0.0;
    }

    public void add(double number) {
        sum += number;
    }
}
