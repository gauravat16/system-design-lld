package ratelimit;

public interface TokenBucketRateLimiter {
    void registerRateLimiter(String id, int bucketSize, int fillRate, long timeInMs);

    boolean acquirePermit(String id);
}
