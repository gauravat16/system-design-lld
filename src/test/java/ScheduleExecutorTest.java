import concurrency.TaskScheduler;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class ScheduleExecutorTest {


    @Test
    public void testTaskScheduler() throws InterruptedException {
        TaskScheduler taskScheduler = new TaskScheduler();
        long time = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(4);
        List<Callable<Integer>> runnables = new ArrayList<>();


        runnables.add(() -> {
            taskScheduler.scheduleTask(time + 8000, () -> {
                System.out.printf("8s : Elapsed time is %d\n", System.currentTimeMillis() - time);
                countDownLatch.countDown();

                taskScheduler.scheduleTask(time + 12000, () -> {
                    System.out.printf("12s: Elapsed time is %d\n", System.currentTimeMillis() - time);
                    countDownLatch.countDown();
                });
            });
            return 0;
        });

        runnables.add(() -> {
            taskScheduler.scheduleTask(time + 3000, () -> {
                System.out.printf("3s: Elapsed time is %d\n", System.currentTimeMillis() - time);
                countDownLatch.countDown();

                taskScheduler.scheduleTask(time + 6000, () -> {
                    System.out.printf("6s: Elapsed time is %d\n", System.currentTimeMillis() - time);
                    countDownLatch.countDown();
                });
            });
            return 0;
        });

        Executors.newFixedThreadPool(3).invokeAll(runnables);

        countDownLatch.await();
    }
}
