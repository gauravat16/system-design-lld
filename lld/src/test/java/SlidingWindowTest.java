import com.gaurav.ratelimit.SlidingWindowRateLimiter;
import com.gaurav.ratelimit.SlidingWindowRateLimiterImpl;
import org.junit.Assert;
import org.junit.Test;

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
