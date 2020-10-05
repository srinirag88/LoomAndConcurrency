package org.example.concurrency;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.example.concurrency.Utils.getChargeCalculator;
import static org.example.concurrency.Utils.getFederalFundsRate;
import static org.example.concurrency.Utils.getSgdUsd;

public class NonBlockingTamed {

    private static final System.Logger LOGGER = System.getLogger(NonBlockingTamed.class.getName());

    public static void main(String[] args) throws InterruptedException {
        placeDeposit(100);
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * @implNote Hope this would scale things up.
     */
    public static void placeDeposit(final double value) {
        compute("SGD->USD", value)
                .thenCompose(r -> compute("addInterest", r))
                .thenCompose(r -> compute("subtractCharges", r))
                .whenComplete((r, e) -> {
                    if (Objects.nonNull(e)) {
                        LOGGER.log(ERROR, e);
                    } else {
                        publishToSomeRandomQueue(r);
                    }
                });
    }

    public static CompletableFuture<Double> compute(String op, double x) {
        switch (op) {
            case "SGD->USD":
                return supplyAsync(() -> getSgdUsd() * x);
            case "addInterest":
                return supplyAsync(() -> getFederalFundsRate() * x + x);
            case "subtractCharges":
                return supplyAsync(() -> x - getChargeCalculator(x));
        }
        throw new UnsupportedOperationException();
    }


    private static void publishToSomeRandomQueue(double v) {
        LOGGER.log(INFO, "Sending to a random queue " + v);
    }
}
