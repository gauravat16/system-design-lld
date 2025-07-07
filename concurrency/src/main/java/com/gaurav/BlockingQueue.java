package com.gaurav;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Requirement
 * <p>
 * Tread safe
 * Fixed size
 * Generics
 *
 * @param <T>
 */
public class BlockingQueue<T> {

    private final int size;
    private final LinkedList<T> elements;
    private final Lock lock = new ReentrantLock();
    private final Condition isEmpty = lock.newCondition();

    public BlockingQueue(int size) {
        this.size = size;
        this.elements = new LinkedList<>();
    }

    public void add(T element) {
        try {
            lock.lock();
            if (elements.size() < size) {
                elements.add(element);
                isEmpty.signalAll();
            }else {
                throw new IllegalArgumentException("size more than limit");
            }
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();

        while (elements.isEmpty()) {
            isEmpty.await();
        }
        T element = elements.removeFirst();
        lock.unlock();

        return element;
    }
}
