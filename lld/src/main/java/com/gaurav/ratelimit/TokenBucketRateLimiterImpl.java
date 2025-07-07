package com.gaurav.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiterImpl implements TokenBucketRateLimiter {

    private final ConcurrentHashMap<String, TokenBucketInfo> bucketInfoMap;

    public TokenBucketRateLimiterImpl() {
        this.bucketInfoMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerRateLimiter(String id, int bucketSize, int fillRate, long timeInMs) {
        bucketInfoMap.computeIfAbsent(id, _id -> new TokenBucketInfo(id, bucketSize, fillRate, timeInMs, bucketSize, System.currentTimeMillis()));
    }

    @Override
    public boolean acquirePermit(final String id) {
        if (!bucketInfoMap.containsKey(id)) {
            return false;
        }

        synchronized (bucketInfoMap.get(id)) {
            return bucketInfoMap.get(id).acquirePermit(1);
        }
    }

    private static class TokenBucketInfo {
        private final String id;
        private final int bucketSize;
        private final int fillRate;
        private final long timeInMs;
        private int currentBucketSize;
        private long lastTimeStamp;

        public TokenBucketInfo(String id, int bucketSize, int fillRate, long timeInMs,
                               int currentBucketSize, long lastTimeStamp) {
            this.id = id;
            this.bucketSize = bucketSize;
            this.fillRate = fillRate;
            this.timeInMs = timeInMs;
            this.currentBucketSize = currentBucketSize;
            this.lastTimeStamp = lastTimeStamp;
        }

        public String getId() {
            return id;
        }

        public int getBucketSize() {
            return bucketSize;
        }

        public int getFillRate() {
            return fillRate;
        }

        public boolean acquirePermit(int count) {
            synchronized (this) {
                long currTime = System.currentTimeMillis();
                if (currTime - lastTimeStamp > timeInMs) {
                    currentBucketSize = fillRate + currentBucketSize;
                    System.out.printf("Updated currentBucketSize lastTimeStamp : %d, currTime : %d,  currentBucketSize:%d%n", lastTimeStamp,currTime,
                            currentBucketSize);
                    lastTimeStamp = System.currentTimeMillis();
                }

                if (count <= currentBucketSize) {
                    currentBucketSize -= count;
                    System.out.println("Decremented currentBucketSize : " + currentBucketSize);
                    return true;
                }

                return false;
            }
        }
    }
}
