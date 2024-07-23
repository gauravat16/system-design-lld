package concurrency;

import java.time.Instant;
import java.time.ZoneId;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaskScheduler {

    private final Queue<Task> taskQueue = new PriorityQueue<>((t1, t2) -> Math.toIntExact(t1.scheduledTime - t2.scheduledTime));
    private final Lock lock = new ReentrantLock();
    private final Condition readyToExecute = lock.newCondition();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TaskScheduler() {
        executorService.submit(this::execute);
    }

    public void scheduleTask(long scheduledTime, Runnable runnable) {
        try {
            lock.lock();
            Task task = new Task(scheduledTime, runnable);
            if (taskQueue.isEmpty()) {
                taskQueue.offer(task);
                readyToExecute.signalAll();
            } else {
                long delayForTaskToBeExecuted = taskQueue.peek().scheduledTime;
                taskQueue.offer(task);
                if (scheduledTime < delayForTaskToBeExecuted) {
                    readyToExecute.signalAll();
                }
            }
            System.out.printf("Added task in queue %s\n", task);
        } finally {
            lock.unlock();
        }

    }

    private void execute() {
        while (true) {
            try {
                lock.lock();
                if (taskQueue.isEmpty()) {
                    readyToExecute.await();
                }

                long delayForTaskToBeExecuted = taskQueue.peek().scheduledTime;
                if (delayForTaskToBeExecuted < System.currentTimeMillis()) {
                    try {
                        Task task = taskQueue.poll();
                        task.runnable.run();
                    } catch (Throwable throwable) {
                        System.out.println("Error : " + throwable);
                    }
                }
                readyToExecute.await(System.currentTimeMillis() - delayForTaskToBeExecuted, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                System.out.println("Error : " + e);
            } finally {
                lock.unlock();
            }
        }
    }


    private final class Task {
        private final long scheduledTime;
        private final Runnable runnable;

        public Task(long scheduledTime, Runnable runnable) {
            this.scheduledTime = scheduledTime;
            this.runnable = runnable;
        }

        @Override
        public String toString() {
            return "Task{" +
                    "scheduledTime=" + Instant.ofEpochMilli(scheduledTime).atZone(ZoneId.systemDefault()).toLocalDateTime() + '}';
        }
    }
}
