package virtual_threads;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VirtualThreadBenchmark {


    private long performBenchMark(ExecutorService executorService, Runnable runnable, int totalCount) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
        System.out.println("Starting Bench");
        long totalTime = 0;

        List<Callable<Long>> callables = new ArrayList<>();

        for (int i = 0; i < totalCount; i++) {
            callables.add(() -> {
                long runStartTime = System.nanoTime();
                runnable.run();
                return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - runStartTime);
            });
        }

        List<Future<Long>> futures = executorService.invokeAll(callables);

        for (Future<Long> f : futures) {
            totalTime += f.get();
        }

        BigDecimal bd = BigDecimal.valueOf(totalTime);
//        bd = bd.setScale(2, RoundingMode.HALF_UP);


        return System.currentTimeMillis() - startTime;

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

        double timeInMsVirtualThreadIO = virtualThreadBenchmark.performIOBench(Executors.newVirtualThreadPerTaskExecutor(), 1000_000);

        double timeInMsNativeThreadIO = virtualThreadBenchmark.performIOBench(Executors.newFixedThreadPool(1000), 1000_000);

        System.out.printf("timeInMsVirtualThreadCPU : %s\ntimeInMsNativeThreadCPU : %s\ndiff : %s", timeInMsVirtualThreadIO, timeInMsNativeThreadIO, timeInMsVirtualThreadIO-timeInMsNativeThreadIO);

    }
}
