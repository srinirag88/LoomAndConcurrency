package org.example.concurrency;

import static org.example.concurrency.Utils.*;
import static org.example.concurrency.Utils.getChargeCalculator;
import static org.example.concurrency.Utils.getFederalFundsRate;
import static org.example.concurrency.Utils.getSgdUsd;

public class ImperativeBlocking {


    public static void main(String[] args) {
        final var maturityAmount= placeDeposit(100);
        System.out.println(maturityAmount);
    }

    public static double placeDeposit(final double value) {
        var result = compute("SGD->USD", value);
        result = compute("addInterest", result);
        result = compute("subtractCharges", result);
        return result;
    }


    public static double compute(String op, double x) {
        switch (op) {
            case "SGD->USD":
                return getSgdUsd() * x;
            case "addInterest":
                return getFederalFundsRate() * x + x;
            case "subtractCharges":
                return x - getChargeCalculator(x);
            default:
        }
        throw new UnsupportedOperationException();
    }
}
