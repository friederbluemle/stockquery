package org.fbluemle.android.stockquery;

import java.util.Locale;

public class StockQueryResponder {
    public String query(String input) {
        return getSymbol(input);
    }

    private String getSymbol(String input) {
        if ("google".equalsIgnoreCase(input)) {
            return "GOOG";
        } else if ("amazon".equalsIgnoreCase(input)) {
            return "AMZN";
        } else if ("apple".equalsIgnoreCase(input)) {
            return "AAPL";
        } else if ("microsoft".equalsIgnoreCase(input)) {
            return "MSFT";
        } else {
            return input.toUpperCase(Locale.getDefault());
        }
    }
}
