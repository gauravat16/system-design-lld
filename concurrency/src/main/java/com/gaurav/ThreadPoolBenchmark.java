package com.gaurav;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ThreadPoolBenchmark {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    private long performBenchMark(ExecutorService executorService, Runnable runnable, int totalCount) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();
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

        return System.currentTimeMillis() - startTime;

    }


    public double performIOBench(ExecutorService executorService, List<Integer> waitTimes, int totalCount) throws ExecutionException, InterruptedException {
        return performBenchMark(executorService, () -> {
            try {
                int waitTime = waitTimes.get(ThreadLocalRandom.current().nextInt(0, waitTimes.size()));
                Thread.sleep(waitTime / 2);
                String data = OBJECT_MAPPER.writeValueAsString(TestClass.getTestClassObj());
                Thread.sleep(waitTime / 2);
                TestClass testClass = OBJECT_MAPPER.readValue(data, TestClass.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, totalCount);
    }

    public double performWithLockingIOBench(ExecutorService executorService, List<Integer> waitTimes, int totalCount, Map<Integer, Lock> lockMap) throws ExecutionException, InterruptedException {
        return performBenchMark(executorService, () -> {
            Lock lock = lockMap.get(Math.abs(Thread.currentThread().toString().hashCode() % lockMap.size()));

            try {
                int waitTime = waitTimes.get(ThreadLocalRandom.current().nextInt(0, waitTimes.size()));
                Thread.sleep(waitTime / 2);
                String data = OBJECT_MAPPER.writeValueAsString(TestClass.getTestClassObj());
                lock.lock();
                Thread.sleep(waitTime / 2);
                TestClass testClass = OBJECT_MAPPER.readValue(data, TestClass.class);
                lock.unlock();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, totalCount);
    }

    private void runBenchmarkForAPILatencyProfile(Map<Integer, Integer> apiLatencyProfile, int qpsToBeAchieved) throws ExecutionException, InterruptedException {
        List<Integer> latencies = generateLatencyList(apiLatencyProfile.get(50), apiLatencyProfile.get(75), apiLatencyProfile.get(90)
                , apiLatencyProfile.get(95), apiLatencyProfile.get(99), qpsToBeAchieved);
        System.out.println(latencies.size());

        for (Map.Entry<Integer, Integer> profile : apiLatencyProfile.entrySet()) {
            double threads = qpsToBeAchieved / (1000D / profile.getValue());
            ExecutorService threadpool = Executors.newFixedThreadPool((int) threads);


            double totalTime = performIOBench(threadpool, latencies, qpsToBeAchieved);


            System.out.printf("Time taken for p%s : %s and threads %s : %s\n", profile.getKey(), profile.getValue(), threads, totalTime);
            threadpool.shutdownNow();
            threadpool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    private void runBenchmarkForAPILatencyProfileWithLocking(Map<Integer, Integer> apiLatencyProfile, int qpsToBeAchieved, Map<Integer, Lock> lockMap) throws ExecutionException, InterruptedException {
        List<Integer> latencies = generateLatencyList(apiLatencyProfile.get(50), apiLatencyProfile.get(75), apiLatencyProfile.get(90)
                , apiLatencyProfile.get(95), apiLatencyProfile.get(99), qpsToBeAchieved);
        System.out.println(latencies.size());

        for (Map.Entry<Integer, Integer> profile : apiLatencyProfile.entrySet()) {
            double threads = qpsToBeAchieved / (1000D / profile.getValue());
            threads = threads < 1 ? 1 : threads;
            ExecutorService threadpool = Executors.newFixedThreadPool((int) threads);


            double totalTime = performWithLockingIOBench(threadpool, latencies, qpsToBeAchieved, lockMap);


            System.out.printf("Time taken for p%s : %s and threads %s : %s\n", profile.getKey(), profile.getValue(), threads, totalTime);
            threadpool.shutdown();
            threadpool.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    public static List<Integer> generateLatencyList(int p50, int p75, int p90, int p95, int p99, int n) {
        List<Integer> latencies = new ArrayList<>(Collections.nCopies(n, 0));

        // Assign values based on percentiles
        latencies.set((int) (0.50 * n) - 1, p50);  // p50 index
        latencies.set((int) (0.75 * n) - 1, p75);  // p75 index
        latencies.set((int) (0.90 * n) - 1, p90);  // p90 index
        latencies.set((int) (0.95 * n) - 1, p95);  // p95 index
        latencies.set((int) (0.99 * n) - 1, p99);  // p99 index

        // Fill in the gaps between percentiles
        for (int i = 0; i < n; i++) {
            if (i < (int) (0.50 * n) - 1) {
                latencies.set(i, p50);  // Values below p50
            } else if (i > (int) (0.50 * n) - 1 && i < (int) (0.75 * n) - 1) {
                latencies.set(i, p50);  // Values between p50 and p75
            } else if (i > (int) (0.75 * n) - 1 && i < (int) (0.90 * n) - 1) {
                latencies.set(i, p75);  // Values between p75 and p90
            } else if (i > (int) (0.90 * n) - 1 && i < (int) (0.95 * n) - 1) {
                latencies.set(i, p90);  // Values between p90 and p95
            } else if (i > (int) (0.95 * n) - 1 && i < (int) (0.99 * n) - 1) {
                latencies.set(i, p95);  // Values between p95 and p99
            } else if (i > (int) (0.99 * n) - 1) {
                latencies.set(i, p99);  // Values above p99
            }
        }

        return latencies;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolBenchmark threadPoolBenchmark = new ThreadPoolBenchmark();
        Map<Integer, Integer> tailLatentApiLatencyProfile = new LinkedHashMap<>();
        tailLatentApiLatencyProfile.put(99, 1000);
        tailLatentApiLatencyProfile.put(95, 700);
        tailLatentApiLatencyProfile.put(90, 500);
        tailLatentApiLatencyProfile.put(75, 300);
        tailLatentApiLatencyProfile.put(50, 250);

        Map<Integer, Integer> cacheOperationLatencyProfile = new LinkedHashMap<>();
        cacheOperationLatencyProfile.put(99, 20);
        cacheOperationLatencyProfile.put(95, 15);
        cacheOperationLatencyProfile.put(90, 10);
        cacheOperationLatencyProfile.put(75, 5);
        cacheOperationLatencyProfile.put(50, 1);


        Map<Integer, Lock> lockMap = new ConcurrentHashMap<>();
        IntStream.range(0, 20).forEach(value -> lockMap.computeIfAbsent(value, _ -> new ReentrantLock()));

        threadPoolBenchmark.runBenchmarkForAPILatencyProfileWithLocking(cacheOperationLatencyProfile, 10, lockMap);
        threadPoolBenchmark.runBenchmarkForAPILatencyProfileWithLocking(cacheOperationLatencyProfile, 2000, lockMap);
//        threadPoolBenchmark.runBenchmarkForAPILatencyProfile(tailLatentApiLatencyProfile, 2000);
//        threadPoolBenchmark.runBenchmarkForAPILatencyProfile(tailLatentApiLatencyProfile, 2000);

//
//        ExecutorService bigPool = Executors.newFixedThreadPool(400);
//        ExecutorService coreSizedPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

//
//        List<int[]> threadTimeList = List.of(new int[]{1, 1000}, new int[]{1, 10000}, new int[]{5, 10000}, new int[]{100, 1000}, new int[]{500, 1000});
//
//        for (int[] threadTime : threadTimeList) {
//            System.out.printf("\nthreadTime %s\n", Arrays.toString(threadTime));
//
//            double timeInMsBigPool = threadPoolBenchmark.performIOBench(bigPool, threadTime[0], threadTime[1]);
////            double timeInMsCorePool = threadPoolBenchmark.performIOBench(coreSizedPool, threadTime[0], threadTime[1]);
//
//            System.out.printf("\ntimeInMsBigPool %s\ntimeInMsCorePool %s\n"
//                    , timeInMsBigPool, 0);
//        }
    }

    static class TestClass {
        private String val1;
        private String val2;
        private String val3;
        private String val4;
        private String val5;
        private String val6;

        public TestClass() {
        }

        public TestClass(String val1, String val2, String val3, String val4, String val5, String val6) {
            this.val1 = val1;
            this.val2 = val2;
            this.val3 = val3;
            this.val4 = val4;
            this.val5 = val5;
            this.val6 = val6;
        }

        public static TestClass getTestClassObj() {
            return new TestClass(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                    UUID.randomUUID().toString());
        }

        public String getVal1() {
            return val1;
        }

        public void setVal1(String val1) {
            this.val1 = val1;
        }

        public String getVal2() {
            return val2;
        }

        public void setVal2(String val2) {
            this.val2 = val2;
        }

        public String getVal3() {
            return val3;
        }

        public void setVal3(String val3) {
            this.val3 = val3;
        }

        public String getVal4() {
            return val4;
        }

        public void setVal4(String val4) {
            this.val4 = val4;
        }

        public String getVal5() {
            return val5;
        }

        public void setVal5(String val5) {
            this.val5 = val5;
        }

        public String getVal6() {
            return val6;
        }

        public void setVal6(String val6) {
            this.val6 = val6;
        }
    }
}
