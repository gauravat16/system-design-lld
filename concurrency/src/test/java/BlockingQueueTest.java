
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingQueueTest {

    @Test
    public void addItems_singleThread() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);

        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }

        int count = 10;
        while (count > 0) {
            System.out.println(queue.get());
            count--;
        }
    }

    @Test
    public void addItems_MultiThread() throws InterruptedException, ExecutionException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(20);
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        executorService.execute(() -> {
            for (int i = 0; i < 20; i++) {
                queue.add(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        AtomicInteger count = new AtomicInteger();

        Future<?> future = executorService.submit(() -> {
            while (true) {
                try {
                    System.out.println(queue.get());
                    count.incrementAndGet();
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        });

        future.get();


    }
}
