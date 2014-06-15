package org.fbluemle.android.stockquery;

public class StockQueryResponder {
    public String query(String input) {
        return getSymbol(input);
    }

    private String getSymbol(String input) {
        if ("google".equalsIgnoreCase(input)) {
            return "GOOG";
        } else {
            return input.toUpperCase();
        }
    }
}
