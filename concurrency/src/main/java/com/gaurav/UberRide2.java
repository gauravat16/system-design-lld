package com.gaurav;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Imagine at the end of a political conference, republicans and democrats are trying to leave the venue and ordering
 * Uber rides at the same time. However, to make sure no fight breaks out in an Uber ride, the software developers at
 * Uber come up with an algorithm whereby either an Uber ride can have all democrats or republicans or two Democrats
 * and two Republicans. All other combinations can result in a fist-fight.
 */
public class UberRide2 {
    private final Semaphore availableDemocrats;
    private final Semaphore availableRepublicans;
    private final Lock lock;
    private final CyclicBarrier isUberFull;
    private int republicans = 0, democrats = 0;

    public UberRide2() {
        this.availableDemocrats = new Semaphore(0);
        this.availableRepublicans = new Semaphore(0);
        this.lock = new ReentrantLock();
        this.isUberFull = new CyclicBarrier(4);
    }


    public void seatRepublican() throws InterruptedException, BrokenBarrierException {
        lock.lock();
        boolean isCarFull = false;

        republicans++;
        if (republicans == 4) {
            availableRepublicans.release(3);
            republicans -= 4;
            isCarFull = true;
        } else if (republicans == 2 && democrats >= 2) {
            availableRepublicans.release(1);
            availableDemocrats.release(2);
            republicans -= 2;
            democrats -= 2;
            isCarFull = true;
        } else {
            lock.unlock();
            availableRepublicans.acquire();
        }
        System.out.printf("%s - Seated!\n", Thread.currentThread().getName());

        isUberFull.await();

        if (isCarFull) {
            System.out.printf("%s - Riding now!\n", Thread.currentThread().getName());
            lock.unlock();
        }

    }


    public void seatDemocrat() throws InterruptedException, BrokenBarrierException {
        lock.lock();
        boolean isCarFull = false;

        democrats++;
        if (democrats == 4) {
            availableDemocrats.release(3);
            democrats -= 4;
            isCarFull = true;
        } else if (democrats == 2 && republicans >= 2) {
            availableDemocrats.release(1);
            availableRepublicans.release(2);
            democrats -= 2;
            republicans -= 2;
            isCarFull = true;
        } else {
            lock.unlock();
            availableDemocrats.acquire();

        }
        System.out.printf("%s - Seated!\n", Thread.currentThread().getName());

        isUberFull.await();


        if (isCarFull) {
            System.out.printf("%s - Riding now!\n", Thread.currentThread().getName());
            lock.unlock();
        }
    }

}
