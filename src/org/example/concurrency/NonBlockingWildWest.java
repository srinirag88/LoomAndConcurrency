package org.example.concurrency;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

public class NonBlockingWildWest {

    private static final System.Logger LOGGER = System.getLogger(NonBlockingWildWest.class.getName());

    public static void main(String[] args) throws InterruptedException {
        placeDeposit(100);
        TimeUnit.SECONDS.sleep(2);
    }


    public static void placeDeposit(double value) {
        compute("SGD->USD", value, (r, e) -> {
            LOGGER.log(INFO, "SGD->USD");
            if (Objects.nonNull(e)) {
                LOGGER.log(ERROR, e);
            } else {
                compute("addInterest", r, (r1, e1) -> {
                    LOGGER.log(INFO, "addInterest");
                    if (Objects.nonNull(e1)) {
                        LOGGER.log(ERROR, e1);
                    } else {
                        compute("subtractCharges", r1, (r2, e2) -> {
                            LOGGER.log(INFO, "subtractCharges");
                            if (Objects.nonNull(e2)) {
                                LOGGER.log(ERROR, e2);
                            } else {
                                publishToSomeRandomQueue(r2);
                            }
                        });
                    }
                });
            }
        });
    }

    static void compute(String op, double x, BiConsumer<Double, Exception> blk) {
        switch (op) {
            case "SGD->USD":
                blk.accept(x, null);
            case "addInterest":
                blk.accept(x, null);
            case "subtractCharges":
                blk.accept(x, null);
        }

    }

    private static void publishToSomeRandomQueue(double v) {
        LOGGER.log(INFO, "Sending to a random queue");
    }

}


