package com.qa.homework.listeners;

import com.qa.homework.driver.DriverFactory;
import io.qameta.allure.Allure;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Attachment;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class ScreenshotListener implements TestLifecycleListener {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");
    private static final Set<Status> FAILURE_STATUSES =
            EnumSet.of(Status.FAILED, Status.BROKEN);

    @Override
    public void beforeTestStop(TestResult result) {
        if (!FAILURE_STATUSES.contains(result.getStatus()) || !DriverFactory.hasDriver()) {
            return;
        }

        String fileName = result.getName()
                + "-" + browserName(result)
                + "-" + LocalDateTime.now().format(FORMATTER)
                + ".png";

        Path screenshotDirectory = Path.of("target", "screenshots");
        Path destination = screenshotDirectory.resolve(fileName);

        try {
            Files.createDirectories(screenshotDirectory);
            byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver())
                    .getScreenshotAs(OutputType.BYTES);
            Files.write(destination, screenshot);
            attachScreenshot(result, screenshot);
            System.out.println("Failure screenshot: " + destination.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not capture screenshot: " + e.getMessage());
        }
    }

    private void attachScreenshot(TestResult result, byte[] screenshot) {
        String source = UUID.randomUUID() + "-attachment.png";

        result.getAttachments().add(new Attachment()
                .setName("Failure screenshot")
                .setType("image/png")
                .setSource(source)
                .setSize((long) screenshot.length));

        Allure.getLifecycle()
                .writeAttachment(source, new ByteArrayInputStream(screenshot));
    }

    private String browserName(TestResult result) {
        return DriverFactory.currentBrowser()
                .orElseGet(() -> result.getParameters().stream()
                .filter(parameter -> "browser".equals(parameter.getName()))
                .map(Parameter::getValue)
                .findFirst()
                .orElse("unknown-browser"));
    }
}
