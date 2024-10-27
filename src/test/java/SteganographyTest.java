import static org.junit.jupiter.api.Assertions.*;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;

class SteganographyTest {

    private SteganographyImage steganographyImage;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        // Create a larger test image (4x4) to accommodate longer messages
        testImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

        // Fill with white pixels
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                testImage.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        steganographyImage = new SteganographyImage(testImage);
    }

    @Test
    @DisplayName("Test hiding a short message")
    void testHideShortMessage() {
        String message = "Hi";
        // Store original pixels
        int[][] originalPixels = new int[4][4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                originalPixels[x][y] = testImage.getRGB(x, y);
            }
        }

        BufferedImage resultImage = Steganography.hideMessage(steganographyImage, message);

        // Compare at bit level
        boolean foundModification = false;
        for (int x = 0; x < 4 && !foundModification; x++) {
            for (int y = 0; y < 4; y++) {
                int original = originalPixels[x][y];
                int modified = resultImage.getRGB(x, y);
                // Compare the LSBs of color channels
                if ((original & 1) != (modified & 1)) {
                    foundModification = true;
                    break;
                }
            }
        }
        assertTrue(foundModification, "At least one pixel should be modified");
    }
//
//    @Test
//    @DisplayName("Test hiding a short message")
//    void testHideShortMessage() {
//        String message = "Hi";
//        BufferedImage resultImage = Steganography.hideMessage(steganographyImage, message);
//
//        // Image dimensions should remain the same
//        assertEquals(testImage.getWidth(), resultImage.getWidth());
//        assertEquals(testImage.getHeight(), resultImage.getHeight());
//
//        // Image should be modified (at least some pixels should be different)
//        boolean foundModification = false;
//        for (int x = 0; x < 4 && !foundModification; x++) {
//            for (int y = 0; y < 4; y++) {
//                if (testImage.getRGB(x, y) != resultImage.getRGB(x, y)) {
//                    foundModification = true;
//                    break;
//                }
//            }
//        }
//        assertTrue(foundModification, "Image should be modified after hiding message");
//    }

    @Test
    @DisplayName("Test message too long for image")
    void testMessageTooLong() {
        // Create a message that's definitely too long for a 4x4 image
        // 4x4 image = 16 pixels * 3 channels = 48 bits capacity
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longMessage.append("A");
        }

        assertThrows(IllegalArgumentException.class, () -> {
            Steganography.hideMessage(steganographyImage, longMessage.toString());
        });
    }

    @Test
    @DisplayName("Test random position generation")
    void testRandomPositionGeneration() {
        Random random = new Random(12345); // Same seed as in your implementation
        String channel = "blue";

        // Get first position
        int pos1 = Steganography.getAndMarkRandomPosition(random, steganographyImage, channel);
        assertTrue(steganographyImage.isPositionUsed(channel, pos1));

        // Get second position
        int pos2 = Steganography.getAndMarkRandomPosition(random, steganographyImage, channel);
        assertTrue(steganographyImage.isPositionUsed(channel, pos2));

        // Positions should be different
        assertNotEquals(pos1, pos2);
    }

    @Test
    @DisplayName("Test deterministic behavior with same seed")
    void testDeterministicBehavior() {
        String message = "Test";

        // Hide message twice with same seed
        BufferedImage result1 = Steganography.hideMessage(steganographyImage, message);

        // Reset image and hide again
        steganographyImage = new SteganographyImage(testImage);
        BufferedImage result2 = Steganography.hideMessage(steganographyImage, message);

        // Results should be identical
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                assertEquals(result1.getRGB(x, y), result2.getRGB(x, y),
                        String.format("Pixels different at (%d,%d)", x, y));
            }
        }
    }

    @Test
    @DisplayName("Test message with special characters")
    void testSpecialCharacters() {
        // Using shorter message with special characters
        String message = "@#$";  // Just 3 characters
        assertDoesNotThrow(() -> {
            Steganography.hideMessage(steganographyImage, message);
        });
    }

    @Test
    @DisplayName("Test null message handling")
    void testNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            Steganography.hideMessage(steganographyImage, null);
        });
    }

    @Test
    @DisplayName("Test maximum capacity message")
    void testMaxCapacityMessage() {
        // Calculate maximum message length
        int maxBits = steganographyImage.getTotalPixels() * 3; // 3 channels
        int maxBytes = maxBits / 8;
        StringBuilder maxMessage = new StringBuilder();
        for (int i = 0; i < maxBytes; i++) {
            maxMessage.append("A");
        }

        assertDoesNotThrow(() -> {
            Steganography.hideMessage(steganographyImage, maxMessage.toString());
        });
    }
}