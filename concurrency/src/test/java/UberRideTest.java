import com.gaurav.UberRide2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UberRideTest {

    @Test
    public void parallel_users() throws ExecutionException, InterruptedException {
        UberRide2 uberRide = new UberRide2();

        ExecutorService democrats = getExecutorService("democrat", 10);
        ExecutorService republicans = getExecutorService("republican", 10);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(democrats.submit(() -> {
                try {
                    uberRide.seatDemocrat();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
            futures.add(republicans.submit(() -> {
                try {
                    uberRide.seatRepublican();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        for (Future<?> f : futures) {
            f.get();
        }
    }

    @Test
    public void onlyDemocrats_users() throws ExecutionException, InterruptedException {
        UberRide2 uberRide = new UberRide2();

        ExecutorService executorService = getExecutorService("democrats", 10);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    uberRide.seatDemocrat();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
    }

    @Test
    public void onlyRepublicans_users() throws ExecutionException, InterruptedException {
        UberRide2 uberRide = new UberRide2();

        ExecutorService executorService = getExecutorService("republican", 10);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    uberRide.seatRepublican();
                } catch (BrokenBarrierException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
    }

    private ExecutorService getExecutorService(String name, int size) {
        return new ThreadPoolExecutor(size, size, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {

            private final AtomicInteger atomicInteger = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name + "-" + atomicInteger.incrementAndGet());
            }
        });
    }
}
