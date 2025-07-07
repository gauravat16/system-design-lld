import concurrency.UnixSexBathroom;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class UniSexBathroomTest {


    @Test
    void testMultithreaded() throws ExecutionException, InterruptedException {
        UnixSexBathroom unixSexBathroom = new UnixSexBathroom(3);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Integer>> runnables = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            runnables.add(() -> {
                unixSexBathroom.useBathRoom(UnixSexBathroom.Gender.F);
                return 0;
            });

//            runnables.add(() -> {
//                unixSexBathroom.useBathRoom(UnixSexBathroom.Gender.M);
//                return 0;
//            });

        }

        for (int i = 0; i < 3; i++) {
//            runnables.add(() -> {
//                unixSexBathroom.useBathRoom(UnixSexBathroom.Gender.F);
//                return 0;
//            });

            runnables.add(() -> {
                unixSexBathroom.useBathRoom(UnixSexBathroom.Gender.M);
                return 0;
            });

        }

        Collections.shuffle(runnables);

        List<Future<?>> tasks = new ArrayList<>(executorService.invokeAll(runnables));

        for (Future<?> future : tasks) {
            future.get();
        }
    }
}
