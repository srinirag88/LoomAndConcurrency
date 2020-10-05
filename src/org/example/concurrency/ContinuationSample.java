package org.example.concurrency;

import java.util.concurrent.Executors;

public class ContinuationSample {

    public static void main(String[] args) {
        final var scope = new ContinuationScope("Test");
        final var continuation = new Continuation(scope, () -> {
            System.out.println("Before");
            Continuation.yield(scope);
            System.out.println("After");
        });
        continuation.run();
        System.out.println(continuation.isDone());
        continuation.run();
        System.out.println(continuation.isDone());
    }
}
