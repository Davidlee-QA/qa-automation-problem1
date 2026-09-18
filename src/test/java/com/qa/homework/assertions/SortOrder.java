package com.qa.homework.assertions;

import java.util.List;

public final class SortOrder {

    private SortOrder() {
    }

    public static boolean isAscending(List<Double> values) {
        for (int i = 1; i < values.size(); i++) {
            if (values.get(i - 1) > values.get(i)) {
                return false;
            }
        }
        return true;
    }
}
