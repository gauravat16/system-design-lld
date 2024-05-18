import org.junit.Assert;
import org.junit.Test;
import ratelimit.LeakingBucketRateLimiter;
import ratelimit.LeakingBucketRateLimiterImpl;
import ratelimit.SlidingWindowRateLimiter;
import ratelimit.SlidingWindowRateLimiterImpl;

import java.util.concurrent.CountDownLatch;

public class SlidingWindowTest {

    @Test
    public void testRlim() throws InterruptedException {
        SlidingWindowRateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiterImpl();
        slidingWindowRateLimiter.registerRateLimiter("1", 4, 1000);
        Assert.assertTrue(slidingWindowRateLimiter.acquirePermit("1"));
        Assert.assertTrue(slidingWindowRateLimiter.acquirePermit("1"));

        Assert.assertTrue(slidingWindowRateLimiter.acquirePermit("1"));

        Assert.assertTrue(slidingWindowRateLimiter.acquirePermit("1"));
        Assert.assertFalse(slidingWindowRateLimiter.acquirePermit("1"));

        Thread.sleep(1000);
        Assert.assertTrue(slidingWindowRateLimiter.acquirePermit("1"));


    }
}
