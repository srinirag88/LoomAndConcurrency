package org.example.concurrency;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Locks {


    private String sharedState;

    public static void main(String[] args) {
        final var locks = new Locks();
        locks.lockExample();
    }

    public void lockExample() {
        var deadline = Instant.now().plusSeconds(10);
        var lock = new ReentrantLock();
        final var exec = Executors.newVirtualThreadExecutor().withDeadline(deadline);
        System.out.println("Start Structure");
        try (exec) {
            exec.submit(() -> {
                lock.lock();
                sharedState = "Task 1 Its mine now";
                System.out.println(sharedState);
                lock.unlock();
            });
            exec.submit(() -> {
                lock.lock();
                sharedState = "Task 2 Its mine now";
                System.out.println(sharedState);
                lock.unlock();
            });
        }
        System.out.println("End Structure");
        System.out.println("After the concurrent updates now I am " + sharedState);
    }
}
