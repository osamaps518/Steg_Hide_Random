import static org.junit.jupiter.api.Assertions.*;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import com.stegrandom.utilites.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.awt.Color;

class SteganographyExtractionTest {

    private SteganographyImage steganographyImage;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        // Create a 4x4 test image
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
    @DisplayName("Test basic message round trip")
    void testBasicRoundTrip() {
        String originalMessage = "Hi";
        StringBuilder messageBits = Utils.convertStringToBits(originalMessage);
        int messageLength = messageBits.length();

        // Hide message
        BufferedImage modifiedImage = Steganography.hideMessage(steganographyImage, originalMessage);

        // Create new SteganographyImage for extraction
        SteganographyImage extractionImage = new SteganographyImage(modifiedImage);

        // Extract message
        String extractedMessage = Steganography.extractMessage(extractionImage, messageLength);

        assertEquals(originalMessage, extractedMessage);
    }

    @Test
    @DisplayName("Test special characters round trip")
    void testSpecialCharactersRoundTrip() {
        String originalMessage = "@#$";
        StringBuilder messageBits = Utils.convertStringToBits(originalMessage);
        int messageLength = messageBits.length();

        BufferedImage modifiedImage = Steganography.hideMessage(steganographyImage, originalMessage);
        SteganographyImage extractionImage = new SteganographyImage(modifiedImage);

        String extractedMessage = Steganography.extractMessage(extractionImage, messageLength);
        assertEquals(originalMessage, extractedMessage);
    }

    @Test
    @DisplayName("Test invalid message length")
    void testInvalidMessageLength() {
        SteganographyImage image = new SteganographyImage(testImage);

        assertThrows(IllegalArgumentException.class, () -> {
            Steganography.extractMessage(image, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Steganography.extractMessage(image, -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Steganography.extractMessage(image, Integer.MAX_VALUE);
        });
    }

    @Test
    @DisplayName("Test maximum capacity message round trip")
    void testMaxCapacityRoundTrip() {
        // Calculate maximum message size for 4x4 image
        int maxBits = steganographyImage.getTotalPixels() * 3; // 3 channels
        int maxChars = maxBits / 8;
        StringBuilder maxMessage = new StringBuilder();
        for (int i = 0; i < maxChars; i++) {
            maxMessage.append("A");
        }

        String originalMessage = maxMessage.toString();
        StringBuilder messageBits = Utils.convertStringToBits(originalMessage);
        int messageLength = messageBits.length();

        BufferedImage modifiedImage = Steganography.hideMessage(steganographyImage, originalMessage);
        SteganographyImage extractionImage = new SteganographyImage(modifiedImage);

        String extractedMessage = Steganography.extractMessage(extractionImage, messageLength);
        assertEquals(originalMessage, extractedMessage);
    }

    @Test
    @DisplayName("Test extraction with wrong length")
    void testExtractionWithWrongLength() {
        String originalMessage = "Test";
        BufferedImage modifiedImage = Steganography.hideMessage(steganographyImage, originalMessage);
        SteganographyImage extractionImage = new SteganographyImage(modifiedImage);

        StringBuilder messageBits = Utils.convertStringToBits(originalMessage);
        int correctLength = messageBits.length();

        // Try extracting with wrong length
        String extractedWrong = Steganography.extractMessage(extractionImage, correctLength + 8);
        assertNotEquals(originalMessage, extractedWrong);
    }
    @Test
    @DisplayName("Test multi-word message round trip")
    void testMultiWordRoundTrip() {
        String originalMessage = "Hi Ant"; // 6 characters including space
        StringBuilder messageBits = Utils.convertStringToBits(originalMessage);
        int messageLength = messageBits.length();

        BufferedImage modifiedImage = Steganography.hideMessage(steganographyImage, originalMessage);
        SteganographyImage extractionImage = new SteganographyImage(modifiedImage);

        String extractedMessage = Steganography.extractMessage(extractionImage, messageLength);
        assertEquals(originalMessage, extractedMessage);
    }
}