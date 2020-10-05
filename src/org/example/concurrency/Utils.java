package org.example.concurrency;

import static java.lang.System.Logger.Level.INFO;

public class Utils {
    private static final System.Logger LOGGER = System.getLogger(Utils.class.getName());


    public static double getSgdUsd() {
        LOGGER.log(INFO, "Calling the currency convertion api ");
        return .70;
    }

    protected static double getFederalFundsRate() {
        LOGGER.log(INFO, "Calling the federal fund api ");
        return 0;
    }

    protected static double getChargeCalculator(double x) {
        LOGGER.log(INFO, "Calling the Charge calculator api ");
        return x * .01;
    }
}

