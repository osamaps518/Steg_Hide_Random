package com.stegrandom.Model;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an image used for steganographic operations, providing methods for pixel manipulation
 * and tracking of modified positions across different color channels (RGB).
 * This class encapsulates all the necessary operations for hiding and extracting data
 * within the image's color channels using the LSB (Least Significant Bit) technique.
 */
public class SteganographyImage {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int totalPixels;
    private final Map<String, BitSet> channelPositions;

    /**
     * Constructs a new SteganographyImage instance from a BufferedImage.
     * Initializes tracking for modified positions in each color channel.
     *
     * @param image The source image to be used for steganography operations.
     */
    public SteganographyImage(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.totalPixels = width * height;
        this.channelPositions = new HashMap<>();
        initializeChannelPositions();
    }

    /**
     * Initializes BitSet tracking for each color channel (RGB).
     * Each BitSet tracks which pixels have been modified in its respective channel.
     */
    private void initializeChannelPositions() {
        channelPositions.put("blue", new BitSet(totalPixels));
        channelPositions.put("red", new BitSet(totalPixels));
        channelPositions.put("green", new BitSet(totalPixels));
    }

    /**
     * Retrieves the RGB value of a pixel at specified coordinates.
     *
     * @param x The x-coordinate of the pixel
     * @param y The y-coordinate of the pixel
     * @return The RGB value of the pixel as an integer
     */
    public int getRGB(int x, int y) {
        return image.getRGB(x, y);
    }

    /**
     * Sets the RGB value of a pixel at specified coordinates.
     *
     * @param x The x-coordinate of the pixel
     * @param y The y-coordinate of the pixel
     * @param rgb The RGB value to set
     */
    public void setRGB(int x, int y, int rgb) {
        image.getRGB(x, y);
        image.setRGB(x, y, rgb);
    }

    /**
     * Converts a linear position to x,y coordinates in the image.
     *
     * @param position The linear position to convert
     * @return An array containing [x, y] coordinates
     */
    public int[] positionToCoordinates(int position) {
        int x = position % width;
        int y = position / width;
        return new int[]{x, y};
    }

    /**
     * Retrieves the BitSet tracking modified positions for a specific color channel.
     *
     * @param channel The color channel ("red", "green", or "blue")
     * @return BitSet tracking modified positions for the specified channel
     */
    public BitSet getChannelPositions(String channel) {
        return channelPositions.get(channel);
    }

    /**
     * Marks a position as used in the specified color channel.
     *
     * @param channel The color channel to mark
     * @param position The position to mark as used
     */
    public void markPositionUsed(String channel, int position) {
        BitSet channelBits = channelPositions.get(channel);
        if (channelBits != null) {
            channelBits.set(position);
        }
    }

    /**
     * Checks if a position has been used in the specified color channel.
     *
     * @param channel The color channel to check
     * @param position The position to check
     * @return true if the position has been used, false otherwise
     */
    public boolean isPositionUsed(String channel, int position) {
        BitSet channelBits = channelPositions.get(channel);
        return channelBits != null && channelBits.get(position);
    }

    /**
     * Extracts individual color components from an RGB value.
     *
     * @param rgb The RGB value to extract colors from
     * @return Map containing alpha, red, green, and blue color values
     */
    public Map<String, Integer> extractColorsFromRGB(int rgb) {
        return Map.of(
                "alpha", (rgb >> 24) & 255,
                "red", (rgb >> 16) & 255,
                "green", (rgb >> 8) & 255,
                "blue", rgb & 255
        );
    }

    /**
     * Reconstructs an RGB value after modifying a specific color channel.
     *
     * @param colors Original color values
     * @param modifiedChannel The channel that was modified
     * @param modifiedValue The new value for the modified channel
     * @return The reconstructed RGB value
     */
    public int reconstructRGB(Map<String, Integer> colors, String modifiedChannel, int modifiedValue) {
        Map<String, Integer> updatedColors = new HashMap<>(colors);
        updatedColors.put(modifiedChannel, modifiedValue);

        return (updatedColors.get("alpha") << 24) |
                (updatedColors.get("red") << 16) |
                (updatedColors.get("green") << 8) |
                updatedColors.get("blue");
    }

    /**
     * Inserts a bit into the least significant bit position of a color value.
     *
     * @param bit The bit to insert (0 or 1)
     * @param color The color value to modify
     * @return The modified color value with the new LSB
     */
    public int insertBitIntoColor(int bit, int color) {
        // 11010101 & 11111110 -> 11010100"clear last bit" | bit -> 1101010bit
        return (color & 0xFE) | bit;
    }

    /**
     * Selects the appropriate color channel based on the bit index.
     * Uses a sequential channel selection strategy: blue -> red -> green.
     *
     * @param bitIndex The index of the bit being processed
     * @return The selected color channel ("blue", "red", or "green")
     */
    public String selectChannel(int bitIndex) {
        if (bitIndex < totalPixels) {
            return "blue";
        } else if (bitIndex < (totalPixels * 2)) {
            return "red";
        } else {
            return "green";
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTotalPixels() {
        return totalPixels;
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Checks if the image can accommodate a message of the given bit length.
     * The capacity is calculated based on using all three color channels (RGB).
     *
     * @param messageBitsLength The length of the message in bits
     * @return true if the message can fit in the image, false otherwise
     */
    public boolean canFitMessage(int messageBitsLength) {
        return messageBitsLength <= (totalPixels * 3);
    }
}