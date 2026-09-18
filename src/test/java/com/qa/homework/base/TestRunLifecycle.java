package com.qa.homework.base;

import com.qa.homework.driver.DriverFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TestRunLifecycle {

    private static final AtomicBoolean PREPARED = new AtomicBoolean(false);
    private static final List<Path> OUTPUT_DIRECTORIES = List.of(
            Path.of("target", "allure-results"),
            Path.of("target", "screenshots"));

    private TestRunLifecycle() {
    }

    public static void prepareRun() {
        if (!PREPARED.compareAndSet(false, true)) {
            return;
        }

        DriverFactory.quitDriver();
        OUTPUT_DIRECTORIES.forEach(TestRunLifecycle::createDirectory);
    }

    public static void cleanUpRun() {
        DriverFactory.quitDriver();
    }

    private static void createDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create test output directory: " + directory, e);
        }
    }
}
