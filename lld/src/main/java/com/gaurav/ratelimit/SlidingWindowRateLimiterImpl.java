package com.gaurav.ratelimit;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowRateLimiterImpl implements SlidingWindowRateLimiter {

    private final ConcurrentHashMap<String, SlidingWindowInfo> bucketInfoMap;

    public SlidingWindowRateLimiterImpl() {
        this.bucketInfoMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerRateLimiter(String id, int bucketSize, long timeInMs) {
        bucketInfoMap.computeIfAbsent(id, _id -> new SlidingWindowInfo(id, bucketSize, timeInMs));
    }

    @Override
    public boolean acquirePermit(final String id) {
        if (!bucketInfoMap.containsKey(id)) {
            return false;
        }
        return bucketInfoMap.get(id).acquirePermit();
    }

    private static class SlidingWindowInfo {
        private final String id;
        private final int bucketSize;
        private final long timeInNano;
        private final SortedSet<Long> timeStamps;

        public SlidingWindowInfo(String id, int bucketSize, long timeInMs) {
            this.id = id;
            this.bucketSize = bucketSize;
            this.timeInNano = timeInMs * 1000_000;
            this.timeStamps = new TreeSet<>();
            System.out.println("timeInNano " + timeInNano);

        }

        public String getId() {
            return id;
        }

        public int getBucketSize() {
            return bucketSize;
        }

        public boolean acquirePermit() {
            synchronized (this) {
                if (timeStamps.isEmpty()) {
                    timeStamps.add(System.nanoTime());
                    System.out.println("Timestamps is empty adding current " + timeStamps);
                    return true;
                } else {
                    long windowStartTime = System.nanoTime() - timeInNano;
                    System.out.println("windowStartTime " + windowStartTime);
                    while (!timeStamps.isEmpty() && timeStamps.first() < windowStartTime) {
                        System.out.println("Removing timestamp " + timeStamps);
                        timeStamps.remove(timeStamps.first());
                    }
                    if (timeStamps.size() < bucketSize) {
                        timeStamps.add(System.nanoTime());
                        System.out.println("Adding timestamp " + timeStamps);
                        return true;
                    }
                    return false;
                }

            }
        }
    }
}
