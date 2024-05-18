package ratelimit;

import java.util.concurrent.Callable;

public interface LeakingBucketRateLimiter<T> {
    void registerRateLimiter(String id, int queueSize, int processRate, long timeInMs);

    boolean submit(String id, Callable<T> callable);
}
