package entregas.atividade1;

import java.util.Random;

public class Atividade implements Runnable {

    private Random random = new Random();
    private int[] array = new int[0];
    private int start = 0;
    private int end = 0;
    private long partialSum;
    private int size = 1_000_000_000;
    private int[] globalArray;

    public Atividade(int[] array, int start, int end){
        this.array = array;
        this.start = start;
        this.end = end;
    }

    public Atividade(){}

    void main() throws InterruptedException {
        globalArray = generateArray(size);
        double sequentialDuration = sequentialSum(globalArray);
        double duration10Threads = concurrentSumWith10Threads(globalArray);
        double duration100Threads = concurrentSumWith100Threads(globalArray);
        double duration10VirtualThreads = concurrentSumWith10VirtualThreads(globalArray);
        double duration100VirtualThreads = concurrentSumWith100VirtualThreads(globalArray);

        System.out.println("------ SPEEDUP ------");
        speedup("10 Threads", sequentialDuration, duration10Threads);
        speedup("100 Threads", sequentialDuration, duration100Threads);
        speedup("10 Virtual Threads", sequentialDuration, duration10VirtualThreads);
        speedup("100 Virtual Threads", sequentialDuration, duration100VirtualThreads);

    }
    @Override
    public void run() {
        partialSum = 0;
        for (int i = start; i < end; i++) {
            partialSum += array[i];
        }
    }

    public long getPartialSum() {
        return partialSum;
    }

    public int[] generateArray(int size) {
        int[] globalArray = new int[size];
        for (int i = 0; i < size; i++) {
            globalArray[i] = random.nextInt(size);
        }
        return globalArray;
    }

    public double sequentialSum(int[] array) {
        long startTime = System.nanoTime();
        long sum = 0;

        for (int value : array) {
            sum += value;
        }

        long endTime = System.nanoTime();
        double duration = nanoToSeconds(startTime, endTime);
        displayResults("Sequential", duration, sum);
        return duration;
    }

    public double concurrentSumWith10Threads(int[] array) throws InterruptedException {
        return concurrentSumWithThreads(array, 10, false, "10 Threads");
    }

    public double concurrentSumWith100Threads(int[] array) throws InterruptedException {
        return concurrentSumWithThreads(array, 100, false, "100 Threads");
    }

    public double concurrentSumWith10VirtualThreads(int[] array) throws InterruptedException {
        return concurrentSumWithThreads(array, 10, true, "10 Virtual Threads");
    }

    public double concurrentSumWith100VirtualThreads(int[] array) throws InterruptedException {
        return concurrentSumWithThreads(array, 100, true, "100 Virtual Threads");
    }

    private double concurrentSumWithThreads(int[] array, int numThreads, boolean virtual, String type) throws InterruptedException {
        long startTime = System.nanoTime();
        long sum = 0;

        Atividade[] activities = new Atividade[numThreads];
        Thread[] threads = new Thread[numThreads];
        int block = array.length / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * block;
            int end = start + block;

            activities[i] = new Atividade(array, start, end);

            if (virtual) {
                threads[i] = Thread.ofVirtual().start(activities[i]);
            } else {
                threads[i] = new Thread(activities[i]);
                threads[i].start();
            }
        }

        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
            sum += activities[i].getPartialSum();
        }

        long endTime = System.nanoTime();
        double duration = nanoToSeconds(startTime, endTime);
        displayResults(type, duration, sum);

        return duration;
    }

    private double nanoToSeconds(long startTime, long endTime) {
        long ns = endTime - startTime;
        return ns / 1_000_000_000.0;
    }

    private void displayResults(String type, double duration, long sum) {
        System.out.printf("--- %s ---\n", type);
        System.out.printf("Tempo: %fs\n", duration);
        System.out.printf("Soma: %d\n", sum);
        System.out.println("-------------------------\n");
    }

    private void speedup(String type, double sequentialDuration, double concurrentDuration) {
        double speedup = sequentialDuration / concurrentDuration;
        System.out.printf("Speedup [%s] = %f\n", type, speedup);
    }

}