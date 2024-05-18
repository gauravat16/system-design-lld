import org.junit.Assert;
import org.junit.Test;
import ratelimit.LeakingBucketRateLimiter;
import ratelimit.LeakingBucketRateLimiterImpl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class LeakingBucketTest {

    @Test
    public void testRlim() throws InterruptedException {
        LeakingBucketRateLimiter<String> leakingBucketRateLimiter = new LeakingBucketRateLimiterImpl<>();
        leakingBucketRateLimiter.registerRateLimiter("1", 2, 1, 1000);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        leakingBucketRateLimiter.submit("1", () -> {
            countDownLatch.countDown();
            System.out.println("1");
            return "1";
        });

       boolean submit =  leakingBucketRateLimiter.submit("1", () -> {
            countDownLatch.countDown();
            System.out.println("2");

            return "1";
        });

        Assert.assertTrue(submit);

        countDownLatch.await();
    }

    @Test
    public void test() throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha1.digest(UUID.randomUUID().toString().getBytes());

        // Convert byte array to hexadecimal string
        StringBuilder sb = new StringBuilder();
        for (byte hashByte : hashBytes) {
            sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        String sha1Hash = sb.toString();

        // Print SHA-1 hash
        System.out.println("SHA-1 Hash: " + sha1Hash);
    }
}
