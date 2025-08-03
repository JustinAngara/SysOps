import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * HANDLE one prompt w/ image to get context, use Google Cloud Vision API for OCR
 */
public class ScreenshotHandler {
    private final int X = 0;
    private final int Y = 0;
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private final String SERVICE_ACCOUNT_EMAIL;
    private final String PROJECT_ID;
    private final PrivateKey PRIVATE_KEY;
    private final String VISION_API_URL = "https://vision.googleapis.com/v1/images:annotate";
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private Robot robot;

    public ScreenshotHandler(String serviceAccountJsonPath) throws Exception {
        // Parse service account JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode serviceAccount = mapper.readTree(new File(serviceAccountJsonPath));

        this.SERVICE_ACCOUNT_EMAIL = serviceAccount.get("client_email").asText();
        this.PROJECT_ID = serviceAccount.get("project_id").asText();
        this.objectMapper = new ObjectMapper();

        // Parse private key
        String privateKeyPem = serviceAccount.get("private_key").asText()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.PRIVATE_KEY = keyFactory.generatePrivate(keySpec);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.robot = new Robot();
    }

    /**
     * Alternative constructor that takes JSON content directly
     */
    public ScreenshotHandler(JsonNode serviceAccountJson) throws Exception {
        this.SERVICE_ACCOUNT_EMAIL = serviceAccountJson.get("client_email").asText();
        this.PROJECT_ID = serviceAccountJson.get("project_id").asText();
        this.objectMapper = new ObjectMapper();

        // Parse private key
        String privateKeyPem = serviceAccountJson.get("private_key").asText()
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.PRIVATE_KEY = keyFactory.generatePrivate(keySpec);

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.robot = new Robot();
    }

    /**
     * Captures a screenshot of the specified screen area
     * @return BufferedImage of the captured screenshot
     */
    public BufferedImage captureScreenshot() {
        Rectangle screenRect = new Rectangle(X, Y, WIDTH, HEIGHT);
        return robot.createScreenCapture(screenRect);
    }

    /**
     * Creates JWT token for Google Cloud authentication
     */
    private String createJWT() throws Exception {
        long now = Instant.now().getEpochSecond();
        long exp = now + 3600; // 1 hour expiration

        // Create JWT header
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes());

        // Create JWT payload
        String payload = String.format(
                "{\"iss\":\"%s\",\"scope\":\"https://www.googleapis.com/auth/cloud-vision\",\"aud\":\"https://oauth2.googleapis.com/token\",\"exp\":%d,\"iat\":%d}",
                SERVICE_ACCOUNT_EMAIL, exp, now
        );
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes());

        // Sign the JWT
        String unsigned = header + "." + encodedPayload;
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(PRIVATE_KEY);
        signature.update(unsigned.getBytes());
        byte[] signedBytes = signature.sign();
        String encodedSignature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signedBytes);

        return unsigned + "." + encodedSignature;
    }

    /**
     * Gets access token using service account
     */
    private String getAccessToken() throws Exception {
        String jwt = createJWT();

        String tokenRequest = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(tokenRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        JsonNode tokenResponse = objectMapper.readTree(response.body());
        return tokenResponse.get("access_token").asText();
    }
    /**
     * Converts BufferedImage to base64 encoded string
     * @param image The image to convert
     * @return Base64 encoded string of the image
     */
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Performs OCR on the image using Google Cloud Vision API with service account auth
     * @param image The screenshot image
     * @return OCR response containing extracted text
     */
    public String performOCR(BufferedImage image) throws Exception {
        String accessToken = getAccessToken();
        String base64Image = imageToBase64(image);

        String jsonPayload = String.format("""
            {
                "requests": [
                    {
                        "image": {
                            "content": "%s"
                        },
                        "features": [
                            {
                                "type": "TEXT_DETECTION"
                            }
                        ]
                    }
                ]
            }
            """, base64Image);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VISION_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     * Extracts just the text from OCR response
     * @param image The screenshot image
     * @return Clean extracted text
     */
    public String extractText(BufferedImage image) throws Exception {
        String ocrResponse = performOCR(image);

        // Parse JSON response to extract text
        JsonNode response = objectMapper.readTree(ocrResponse);
        JsonNode textAnnotations = response.path("responses").get(0).path("textAnnotations");

        if (textAnnotations.size() > 0) {
            return textAnnotations.get(0).path("description").asText();
        }

        return "No text detected";
    }

    /**
     * Convenience method to capture screenshot and perform OCR in one call
     * @return Extracted text from screenshot
     */
    public String captureAndExtractText() throws Exception {
        BufferedImage screenshot = captureScreenshot();
        return extractText(screenshot);
    }

    /**
     * Saves screenshot to file (useful for debugging)
     * @param image The image to save
     * @param filename The filename to save to
     */
    public void saveScreenshot(BufferedImage image, String filename) throws IOException {
        File outputFile = new File(filename);
        ImageIO.write(image, "png", outputFile);
        System.out.println("Screenshot saved to: " + outputFile.getAbsolutePath());
    }

    /**
     * Updates screen dimensions if needed
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of capture area
     * @param height Height of capture area
     */
    public ScreenshotHandler withDimensions(int x, int y, int width, int height) throws Exception {
        // This needs to be implemented properly - for now just return new instance
        throw new UnsupportedOperationException("Use constructor with service account JSON");
    }

    // Example usage
    public static void main(String[] args) {
        try {
            // Initialize with your service account JSON file
            ScreenshotHandler handler = new ScreenshotHandler("C:\\Users\\justi\\IdeaProjects\\SysOps\\xmljson\\xml-maps-proj-19d908ac11ac.json");

            // Capture screenshot and extract text with OCR
            String extractedText = handler.captureAndExtractText();
            System.out.println("Extracted Text: " + extractedText);

            // Or get full OCR response with coordinates
            BufferedImage screenshot = handler.captureScreenshot();
            String fullResponse = handler.performOCR(screenshot);
            System.out.println("Full OCR Response: " + fullResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}