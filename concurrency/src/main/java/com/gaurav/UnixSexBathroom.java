package com.gaurav;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bathroom is being designed for the use of both males and females in an office but requires the following constraints to be maintained:
 * <p>
 * There cannot be men and women in the bathroom at the same time.
 * There should never be more than three employees in the bathroom simultaneously.
 */
public class UnixSexBathroom {

    private int men = 0;
    private int women = 0;
    private int occupentCount = 0;
    private final int maxOccupants;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Semaphore occupants = new Semaphore(3);

    public UnixSexBathroom(int maxOccupants) {
        this.maxOccupants = maxOccupants;
    }

    public enum Gender {
        M, F
    }

    public void useBathRoom(Gender gender) throws InterruptedException {

        lock.lock();
        while (men > 0 && Gender.F == gender || women > 0 && Gender.M == gender) {
            condition.await();
        }
        lock.unlock();

        enterBathroom(gender);
        Thread.sleep(2000);
        exitBathroom(gender);
    }

    private void enterBathroom(Gender gender) throws InterruptedException {
        lock.lock();
        if (Gender.M == gender) {
            men++;
        } else {
            women++;
        }
        System.out.printf("%s - State %s, I am %s, using this bathroom\n", Thread.currentThread().getName(), this, gender);
        occupentCount++;
        occupants.acquire();

        lock.unlock();
    }

    private void exitBathroom(Gender gender) {
        lock.lock();
        occupants.release(1);
        occupentCount--;
        if (Gender.M == gender) {
            men--;
        } else {
            women--;
        }

        System.out.printf("%s - State %s, I am %s, leaving this bathroom\n", Thread.currentThread().getName(), this, gender);
        condition.signalAll();
        lock.unlock();
    }

    @Override
    public String toString() {
        return "UnixSexBathroom{" +
                "men=" + men +
                ", women=" + women +
                ", occupentCount=" + occupentCount +
                '}';
    }
}
