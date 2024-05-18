package ratelimit;

public interface SlidingWindowRateLimiter {
    void registerRateLimiter(String id, int bucketSize, long timeInMs);

    boolean acquirePermit(String id);
}
