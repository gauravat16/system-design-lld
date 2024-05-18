import org.junit.Assert;
import org.junit.Test;
import ratelimit.TokenBucketRateLimiter;
import ratelimit.TokenBucketRateLimiterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TokenBucketTest {

    @Test
    public void test_acquire_token() {
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiterImpl();
        String id = "1";
        tokenBucketRateLimiter.registerRateLimiter(id, 4, 2, 4000);

        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(tokenBucketRateLimiter.acquirePermit(id));
        }
        Assert.assertFalse(tokenBucketRateLimiter.acquirePermit(id));
    }


    @Test
    public void test_acquire_token_fillRate() throws InterruptedException {
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiterImpl();
        String id = "1";
        tokenBucketRateLimiter.registerRateLimiter(id, 4, 2, 1);

        for (int i = 0; i < 4; i++) {
            Assert.assertTrue(tokenBucketRateLimiter.acquirePermit(id));
        }
        Thread.sleep(100);
        Assert.assertTrue(tokenBucketRateLimiter.acquirePermit(id));
    }

    @Test
    public void test_acquire_token_multiThread() throws InterruptedException {
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiterImpl();
        String id = "1";
        tokenBucketRateLimiter.registerRateLimiter(id, 4, 1, 1000);

        ExecutorService executorService = Executors.newFixedThreadPool(6);

        Callable<Integer> callable = () -> tokenBucketRateLimiter.acquirePermit(id) ? 1 : 0;

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            futures.add(executorService.submit(callable));
        }

        Thread.sleep(1000);

        futures.add(executorService.submit(callable));
        futures.add(executorService.submit(callable));


        int sum = futures.stream().map(future -> {
            try {
                int val =  future.get();
                System.out.println(val);
                return val;
            } catch (Exception e) {
                return 0;
            }
        })
                .mapToInt(value -> value)
                .sum();

        Assert.assertEquals(5, sum);

    }
}
