package virtual_threads;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VirtualThreadBenchmark {


    private double performBenchMark(ExecutorService executorService, Runnable runnable, int totalCount) throws InterruptedException, ExecutionException {
        System.out.println("Starting Bench");
        double totalTime = 0;

        List<Callable<Double>> callables = new ArrayList<>();

        for (int i = 0; i < totalCount; i++) {
            callables.add(() -> {
                long startTime = System.nanoTime();
                runnable.run();
                return (System.nanoTime() - startTime) / 1000000.0;
            });
        }

        List<Future<Double>> futures = executorService.invokeAll(callables);

        for (Future<Double> f : futures) {
            totalTime += f.get();
        }

        BigDecimal bd = BigDecimal.valueOf(totalTime);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }


    public double performCPUBench(ExecutorService executorService, int totalCount) throws ExecutionException, InterruptedException {
        return performBenchMark(executorService, () -> {
            Math.pow(100, 100131);
        }, totalCount);
    }

    public double performIOBench(ExecutorService executorService, int totalCount) throws ExecutionException, InterruptedException {
        return performBenchMark(executorService, () -> {
//            System.out.printf("%s doing a task\n", Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, totalCount);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        VirtualThreadBenchmark virtualThreadBenchmark = new VirtualThreadBenchmark();
//        double timeInMsVirtualThreadCPU = virtualThreadBenchmark.performCPUBench(Executors.newVirtualThreadPerTaskExecutor(), 1000_000);
//        double timeInMsNativeThreadCPU = virtualThreadBenchmark.performCPUBench(Executors.newFixedThreadPool(1), 1000_000);
//
//        System.out.printf("timeInMsVirtualThreadCPU : %s\ntimeInMsNativeThreadCPU : %s\ndiff: %s", timeInMsVirtualThreadCPU, timeInMsNativeThreadCPU, timeInMsNativeThreadCPU - timeInMsVirtualThreadCPU);

        double timeInMsVirtualThreadIO = virtualThreadBenchmark.performIOBench(Executors.newVirtualThreadPerTaskExecutor(), 100_00);

        double timeInMsNativeThreadIO = virtualThreadBenchmark.performIOBench(Executors.newFixedThreadPool(10), 100_00);

        System.out.printf("timeInMsVirtualThreadCPU : %s\ntimeInMsNativeThreadCPU : %s\ndiff : %s", timeInMsVirtualThreadIO, timeInMsNativeThreadIO, timeInMsVirtualThreadIO-timeInMsNativeThreadIO);

    }
}
