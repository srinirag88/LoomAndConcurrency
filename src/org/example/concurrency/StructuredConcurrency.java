package org.example.concurrency;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;
import static org.example.concurrency.Utils.getChargeCalculator;
import static org.example.concurrency.Utils.getFederalFundsRate;
import static org.example.concurrency.Utils.getSgdUsd;

public class StructuredConcurrency {


    public static void main(String[] args) {

        //unStructuredConcurrency();

        //structuredConcurrency();

          structuredConcurrencyWithCf();

        //System.out.println(placeDeposit(100));

    }

    public static void unStructuredConcurrency() {
        var deadline = Instant.now().plusSeconds(10);
        final var exec = Executors.newFixedThreadPool(2);
        System.out.println("Start Structure");
        exec.submit(() -> System.out.println("Start task 1"));
        exec.submit(() -> System.out.println("Start task 2"));
        System.out.println("End Structure");
        exec.shutdown();
    }


    public static void structuredConcurrency() {
        var deadline = Instant.now().plusSeconds(10);
        final var exec = Executors.newVirtualThreadExecutor().withDeadline(deadline);
        System.out.println("Start Structure");
        try (exec) {
            exec.submit(() -> System.out.println("Start task 1"));
            exec.submit(() -> System.out.println("Start task 2"));
        }
        System.out.println("End Structure");
    }


    public static void structuredConcurrencyWithCf() {
        var deadline = Instant.now().plusSeconds(10);
        final var exec = Executors.newVirtualThreadExecutor().withDeadline(deadline);
        System.out.println("Start Structure");
        try (exec) {
            final var tasks = IntStream.range(0, 10)
                    .mapToObj(x -> CompletableFuture
                            .runAsync(() ->// Imp
                                    System.out.println("Start Task" + x), exec))
                    .collect(toList());
            tasks.stream().filter(CompletableFuture::isCancelled).forEach(System.out::println);
        }
        System.out.println("End Structure");
    }

    public static Double placeDeposit(final double value) {
        var deadline = Instant.now().plusSeconds(10);
        final var exec = Executors.newVirtualThreadExecutor().withDeadline(deadline);
        try (exec) {
            return compute("SGD->USD", value, exec)
                    .thenCompose(r -> compute("addInterest", r, exec))
                    .thenCompose(r -> compute("subtractCharges", r, exec)).join();
        } catch (CompletionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }


    public static CompletableFuture<Double> compute(String op, double x, Executor exec) {
        switch (op) {
            case "SGD->USD":
                return supplyAsync(() -> getSgdUsd() * x, exec);
            case "addInterest":
                return supplyAsync(() -> getFederalFundsRate() * x + x, exec);
            case "subtractCharges":
                return supplyAsync(() -> x - getChargeCalculator(x), exec);
        }
        throw new UnsupportedOperationException();
    }

}
