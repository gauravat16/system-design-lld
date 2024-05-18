package ratelimit;

import java.util.Queue;
import java.util.concurrent.*;

public class LeakingBucketRateLimiterImpl<T> implements LeakingBucketRateLimiter<T> {

    private final ConcurrentHashMap<String,LeakingBucket<T>> bucketInfoMap;

    public LeakingBucketRateLimiterImpl() {
        this.bucketInfoMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerRateLimiter(String id, int queueSize, int processRate, long timeInMs) {
        bucketInfoMap.computeIfAbsent(id, _id -> new LeakingBucket<T>(queueSize, processRate, timeInMs));
    }

    @Override
    public boolean submit(String id, Callable<T> callable) {
        return bucketInfoMap.get(id).submit(callable);
    }

    private static class LeakingBucket<V> {
        private final Queue<Callable<V>> queue;
        private final int queueSize;
        private final int processRate;
        private final long timeInMs;

        private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        public LeakingBucket(int queueSize, int processRate, long timeInMs) {
            this.queue = new ArrayBlockingQueue<>(queueSize);
            this.queueSize = queueSize;
            this.processRate = processRate;
            this.timeInMs = timeInMs;

            scheduledExecutorService.scheduleAtFixedRate(() -> {
                for (int i = 0; i < processRate; i++) {
                    try {
                        if (queue.isEmpty()) continue;
                        queue.poll().call();
                    } catch (Exception e) {
                        System.out.println("Failed : " + e.getMessage());
                    }
                }
            }, 0,timeInMs, TimeUnit.MILLISECONDS);
        }

        public boolean submit(Callable<V> callable) {
            synchronized (this) {
                if (queue.size() == queueSize) return false;
                return queue.offer(callable);
            }
        }
    }
}
