package com.sysops.functions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * HANDLE one prompt w/ image to get context, use Google Cloud Vision API for com.sysops.functions.OCR
 */
public class ScreenshotHandler {
    private final static int X = 0;
    private final static int Y = 0;
    private final static int WIDTH = 1920;
    private final static int HEIGHT = 1080;
    private final static Robot robot;

    // good now
     static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Captures a screenshot of the specified screen area
     * @return BufferedImage of the captured screenshot
     */
    public static BufferedImage captureScreenshot() {
        Rectangle screenRect = new Rectangle(X, Y, WIDTH, HEIGHT);
        return robot.createScreenCapture(screenRect);
    }

    /**
     * Converts BufferedImage to base64 encoded string
     * @param image The image to convert
     * @return Base64 encoded string of the image
     */
    private static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
//        System.out.println(Base64.getEncoder().encodeToString(imageBytes));
        return Base64.getEncoder().encodeToString(imageBytes);
    }
    /*
    public static void main(String[] args) throws IOException {

        //         prevent compilertime overhead
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long startTime = System.nanoTime();

        com.sysops.functions.ScreenshotHandler.imageToBase64(com.sysops.functions.ScreenshotHandler.captureScreenshot());

        long stopTime = System.nanoTime();
        long durationInMs = TimeUnit.NANOSECONDS.toMillis(stopTime - startTime);
        System.out.println(durationInMs);

    }
     */

}