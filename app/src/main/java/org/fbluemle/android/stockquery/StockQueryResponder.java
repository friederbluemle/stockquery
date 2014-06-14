package org.fbluemle.android.stockquery;

public class StockQueryResponder {
    public String query(String input) {
        String symbol = getSymbol(input);
        if ("GOOG".equals(symbol)) {
            return "551.76 +0.41 (0.07%)";
        } else if ("EBAY".equals(symbol)) {
            return "49.04 +0.48 (0.99%)";
        } else if ("AAPL".equals(symbol)) {
            return "91.28 -1.01 (-1.09%)";
        } else {
            return "Unknown symbol: " + symbol;
        }
    }

    private String getSymbol(String input) {
        if ("google".equalsIgnoreCase(input)) {
            return "GOOG";
        } else {
            return input.toUpperCase();
        }
    }
}
