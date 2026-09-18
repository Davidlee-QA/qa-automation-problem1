package com.qa.homework.data;

import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class LoginDataProvider {

    private static final String INVALID_LOGIN_CASES_FILE = "test-data/invalid-login-cases.csv";
    private static final String LONG_TEXT_TOKEN = "{{longText}}";

    private LoginDataProvider() {
    }

    @DataProvider(name = "invalidLoginCases")
    public static Object[][] invalidLoginCases() {
        return readRows().stream()
                .map(LoginDataProvider::toInvalidLoginCase)
                .toArray(Object[][]::new);
    }

    private static Object[] toInvalidLoginCase(List<String> row) {
        return new Object[]{
                resolveToken(row.get(0)),
                resolveToken(row.get(1)),
                SauceDemoTestData.message(row.get(2))
        };
    }

    private static String resolveToken(String value) {
        if (LONG_TEXT_TOKEN.equals(value)) {
            return SauceDemoTestData.longText();
        }
        return value;
    }

    private static List<List<String>> readRows() {
        try (InputStream input = LoginDataProvider.class
                .getClassLoader()
                .getResourceAsStream(INVALID_LOGIN_CASES_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Test data file not found: " + INVALID_LOGIN_CASES_FILE);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))) {
                return reader.lines()
                        .skip(1)
                        .filter(line -> !line.isBlank())
                        .map(LoginDataProvider::parseCsvLine)
                        .peek(LoginDataProvider::validateRow)
                        .toList();
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load test data file: " + INVALID_LOGIN_CASES_FILE, e);
        }
    }

    private static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                quoted = !quoted;
            } else if (character == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        values.add(current.toString());
        return values;
    }

    private static void validateRow(List<String> row) {
        if (row.size() != 3) {
            throw new IllegalStateException(
                    "Invalid invalid-login test data row. Expected 3 columns but found "
                            + row.size() + ": " + row);
        }
    }
}
