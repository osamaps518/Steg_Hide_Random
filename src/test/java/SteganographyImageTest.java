import static org.junit.jupiter.api.Assertions.*;

import com.stegrandom.Model.SteganographyImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.BitSet;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

class SteganographyImageTest {

    private BufferedImage testImage;
    private SteganographyImage steganographyImage;

    @BeforeEach
    void setUp() {
        // Create a small 2x2 test image
        testImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);

        // Set different colors for each pixel
        testImage.setRGB(0, 0, new Color(255, 0, 0, 255).getRGB());     // Red
        testImage.setRGB(0, 1, new Color(0, 255, 0, 255).getRGB());     // Green
        testImage.setRGB(1, 0, new Color(0, 0, 255, 255).getRGB());     // Blue
        testImage.setRGB(1, 1, new Color(255, 255, 255, 255).getRGB()); // White

        steganographyImage = new SteganographyImage(testImage);
    }

    // Alternative setup using a real image file
    SteganographyImage loadTestImageFromFile() throws IOException {
        BufferedImage image = ImageIO.read(new File("src/test/resources/test-image.png"));
        return new SteganographyImage(image);
    }

    @Test
    @DisplayName("Extract pure red color from top-left pixel")
    void testExtractRedPixel() {
        int rgb = testImage.getRGB(0, 0);
        Map<String, Integer> colors = steganographyImage.extractColorsFromRGB(rgb);

        assertEquals(255, colors.get("alpha"));
        assertEquals(255, colors.get("red"));
        assertEquals(0, colors.get("green"));
        assertEquals(0, colors.get("blue"));
    }

    @Test
    @DisplayName("Extract pure green color from bottom-left pixel")
    void testExtractGreenPixel() {
        int rgb = testImage.getRGB(0, 1);
        Map<String, Integer> colors = steganographyImage.extractColorsFromRGB(rgb);

        assertEquals(255, colors.get("alpha"));
        assertEquals(0, colors.get("red"));
        assertEquals(255, colors.get("green"));
        assertEquals(0, colors.get("blue"));
    }

    @Test
    @DisplayName("Test reconstruction with modified red channel")
    void testReconstructModifiedRed() {
        int originalRgb = testImage.getRGB(0, 0); // Get red pixel
        Map<String, Integer> colors = steganographyImage.extractColorsFromRGB(originalRgb);

        // Modify red to 127
        int reconstructed = steganographyImage.reconstructRGB(colors, "red", 127);
        Map<String, Integer> newColors = steganographyImage.extractColorsFromRGB(reconstructed);

        assertEquals(127, newColors.get("red"));
        assertEquals(0, newColors.get("green"));    // Should remain unchanged
        assertEquals(0, newColors.get("blue"));     // Should remain unchanged
        assertEquals(255, newColors.get("alpha"));  // Should remain unchanged
    }

    @Test
    @DisplayName("Test round trip: extract -> reconstruct without modifications")
    void testRoundTripWithoutModification() {
        int originalRgb = testImage.getRGB(1, 1); // Get white pixel
        Map<String, Integer> colors = steganographyImage.extractColorsFromRGB(originalRgb);
        int reconstructed = steganographyImage.reconstructRGB(colors, "red", colors.get("red"));

        assertEquals(originalRgb, reconstructed);
    }

    @Test
    @DisplayName("Verify image dimensions")
    void testImageDimensions() {
        assertEquals(2, steganographyImage.getWidth());
        assertEquals(2, steganographyImage.getHeight());
        assertEquals(4, steganographyImage.getTotalPixels());
    }

    @Test
    @DisplayName("Test coordinate conversion")
    void testPositionToCoordinates() {
        // For a 2x2 image:
        // Position 0 should be (0,0)
        // Position 1 should be (1,0)
        // Position 2 should be (0,1)
        // Position 3 should be (1,1)

        int[] coord0 = steganographyImage.positionToCoordinates(0);
        assertArrayEquals(new int[]{0, 0}, coord0);

        int[] coord1 = steganographyImage.positionToCoordinates(1);
        assertArrayEquals(new int[]{1, 0}, coord1);

        int[] coord2 = steganographyImage.positionToCoordinates(2);
        assertArrayEquals(new int[]{0, 1}, coord2);

        int[] coord3 = steganographyImage.positionToCoordinates(3);
        assertArrayEquals(new int[]{1, 1}, coord3);
    }

    @Test
    @DisplayName("Test channel position tracking")
    void testChannelPositionTracking() {
        String channel = "blue";
        int position = 0;

        // Initially position should not be used
        assertFalse(steganographyImage.isPositionUsed(channel, position));

        // Mark position as used
        steganographyImage.markPositionUsed(channel, position);

        // Now position should be marked as used
        assertTrue(steganographyImage.isPositionUsed(channel, position));

        // Check BitSet directly
        BitSet channelBits = steganographyImage.getChannelPositions(channel);
        assertTrue(channelBits.get(position));
    }

    @Test
    @DisplayName("Test channel selection logic")
    void testChannelSelection() {
        int totalPixels = steganographyImage.getTotalPixels(); // Should be 4 for 2x2 image

        // First totalPixels bits should use blue channel
        assertEquals("blue", steganographyImage.selectChannel(0));
        assertEquals("blue", steganographyImage.selectChannel(totalPixels - 1));

        // Next totalPixels bits should use red channel
        assertEquals("red", steganographyImage.selectChannel(totalPixels));
        assertEquals("red", steganographyImage.selectChannel(totalPixels * 2 - 1));

        // Remaining bits should use green channel
        assertEquals("green", steganographyImage.selectChannel(totalPixels * 2));
        assertEquals("green", steganographyImage.selectChannel(totalPixels * 3 - 1));
    }

    @Test
    @DisplayName("Test bit insertion into color")
    void testInsertBitIntoColor() {
        // Test inserting 0
        int colorWith0 = steganographyImage.insertBitIntoColor(0, 255);
        assertEquals(254, colorWith0); // 255 with LSB set to 0

        // Test inserting 1
        int colorWith1 = steganographyImage.insertBitIntoColor(1, 254);
        assertEquals(255, colorWith1); // 254 with LSB set to 1

        // Test that only LSB is modified
        int randomColor = 123;
        int modified0 = steganographyImage.insertBitIntoColor(0, randomColor);
        int modified1 = steganographyImage.insertBitIntoColor(1, randomColor);
        assertEquals(122, modified0); // Should clear LSB
        assertEquals(123, modified1); // Should set LSB
    }

    @Test
    @DisplayName("Test message capacity checking")
    void testMessageCapacity() {
        // For 2x2 image:
        // Total pixels = 4
        // Total available bits = 4 * 3 = 12 (using RGB channels)

        assertTrue(steganographyImage.canFitMessage(12)); // Should fit exactly
        assertTrue(steganographyImage.canFitMessage(8));  // Should fit with room
        assertFalse(steganographyImage.canFitMessage(13)); // Should not fit
    }

    @Test
    @DisplayName("Test pixel RGB get/set")
    void testPixelManipulation() {
        int x = 0, y = 0;
        int originalRGB = steganographyImage.getRGB(x, y);
        int newRGB = new Color(100, 100, 100).getRGB();

        steganographyImage.setRGB(x, y, newRGB);
        assertEquals(newRGB, steganographyImage.getRGB(x, y));

        // Restore original color
        steganographyImage.setRGB(x, y, originalRGB);
    }

    @Test
    @DisplayName("Test channel positions initialization")
    void testChannelPositionsInitialization() {
        BitSet blueChannel = steganographyImage.getChannelPositions("blue");
        BitSet redChannel = steganographyImage.getChannelPositions("red");
        BitSet greenChannel = steganographyImage.getChannelPositions("green");

        assertNotNull(blueChannel);
        assertNotNull(redChannel);
        assertNotNull(greenChannel);

        assertEquals(0, blueChannel.cardinality()); // Should be empty initially
        assertEquals(0, redChannel.cardinality());
        assertEquals(0, greenChannel.cardinality());
    }
}