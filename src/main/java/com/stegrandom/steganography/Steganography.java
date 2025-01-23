package com.stegrandom.steganography;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.utilites.Utils;
import org.apache.commons.math3.random.MersenneTwister;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Core steganography implementation class providing methods for hiding and extracting
 * messages within images using the LSB (Least Significant Bit) technique with a
 * pseudo-random distribution pattern.
 */
public class Steganography {

    /**
     * Generates and marks a random unused position in the specified color channel.
     * Uses pseudo-random number generation to distribute message bits across the image.
     * The method ensures the same position isn't used twice within the same channel.
     *
     * @param random The random number generator with a fixed seed for reproducibility
     * @param image The steganography image being processed
     * @param channel The color channel being modified ("red", "green", or "blue")
     * @return A random unused position in the specified channel
     */
    public static int getAndMarkRandomPosition(MersenneTwister random, SteganographyImage image, String channel) {
        int position;
        do {
            position = random.nextInt(image.getTotalPixels());
        } while (image.isPositionUsed(channel, position));

        image.markPositionUsed(channel, position);
        return position;
    }

    /**
     * Hides a secret message within an image using LSB steganography with pseudo-random distribution.
     * The method distributes message bits across the RGB channels sequentially, using
     * random positions within each channel. The process uses a fixed seed for the random
     * number generator to ensure reproducibility during extraction.
     *
     * The algorithm:
     * 1. Converts the message to binary
     * 2. Validates message length against image capacity
     * 3. For each bit:
     *    - Selects appropriate color channel
     *    - Finds random unused position
     *    - Modifies LSB of selected color at position
     *    - Reconstructs and updates pixel
     *
     * @param image The steganography image to hide the message in
     * @param secretMsg The secret message to hide
     * @return The modified image containing the hidden message
     * @throws IllegalArgumentException if the message is null, empty, or too long for the image
     */
    public static BufferedImage hideMessage(SteganographyImage image, String secretMsg) {
        if (secretMsg == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        String messageWithTerminator = secretMsg + "EOF";  // Add a terminator marker
        StringBuilder messageBits = Utils.convertStringToBits(messageWithTerminator);

        if (messageBits == null) {
            throw new IllegalArgumentException("Cannot convert null or empty message to bits");
        }
        int messageLength = messageBits.length();

        // Validate message can fit in image
        if (!image.canFitMessage(messageLength)) {
            throw new IllegalArgumentException("Message too long for this image");
        }

        // Initialize MT19937 with seed
        MersenneTwister random = new MersenneTwister(12345);

        int bitIndex = 0;
        while (bitIndex < messageLength) {
            String colorSelected = image.selectChannel(bitIndex);
            // Get random unused position
            int position = getAndMarkRandomPosition(random, image, colorSelected);
            System.out.println("random number: " + position);



            // Get coordinates and modify pixel
            int[] coordinates = image.positionToCoordinates(position);
            int x = coordinates[0];
            int y = coordinates[1];

            // Get current pixel color values
            int rgb = image.getRGB(x, y);
            Map<String, Integer> colors = image.extractColorsFromRGB(rgb);

            // Modify the selected color channel "convert 0/1 bit from char type to int type"
            int currentBit = Utils.charToDigit(messageBits.charAt(bitIndex));
            int modifiedColor = image.insertBitIntoColor(currentBit, colors.get(colorSelected));

            // Reconstruct and set the modified pixel
            int modifiedRGB = image.reconstructRGB(colors, colorSelected, modifiedColor);
            image.setRGB(x, y, modifiedRGB);

            bitIndex++;
        }

        return image.getImage();
    }

    /**
     * Extracts a hidden message from a steganography image using LSB extraction with
     * pseudo-random distribution matching the hiding process.
     *
     * The method must use the same seed and distribution pattern as the hiding process
     * to successfully extract the message. It follows these steps:
     * 1. Validates the requested message length
     * 2. For each bit position:
     *    - Determines the correct color channel
     *    - Finds the corresponding random position
     *    - Extracts the LSB from the appropriate color value
     * 3. Converts the collected bits back to text
     *
     * @param image The steganography image containing the hidden message
     * @param messageLength The length of the hidden message in bits
     * @return The extracted secret message
     * @throws IllegalArgumentException if messageLength is invalid or too large for the image
     */
    public static String extractMessage(SteganographyImage image, int messageLength) {
        if (messageLength <= 0) {
            throw new IllegalArgumentException("Message length must be positive");
        }

        // Validate message length can fit in image
        if (!image.canFitMessage(messageLength)) {
            throw new IllegalArgumentException("Specified message length is too large for this image");
        }

        // Initialize MT19937 with same seed
        MersenneTwister random = new MersenneTwister(12345);

        // StringBuilder to collect the bits
        StringBuilder extractedBits = new StringBuilder(messageLength);

        int bitIndex = 0;
        while (bitIndex < messageLength) {
            String colorSelected = image.selectChannel(bitIndex);
            // Get random unused position (same sequence as hiding)
            int position = getAndMarkRandomPosition(random, image, colorSelected);

            // Get coordinates
            int[] coordinates = image.positionToCoordinates(position);
            int x = coordinates[0];
            int y = coordinates[1];

            // Get pixel color values
            int rgb = image.getRGB(x, y);
            Map<String, Integer> colors = image.extractColorsFromRGB(rgb);

            // Extract LSB from the selected color channel
            int extractedBit = colors.get(colorSelected) & 1;
            extractedBits.append(extractedBit);

            bitIndex++;
        }

        // Convert bits back to string
        return Utils.convertBitsToString(extractedBits);
    }
}