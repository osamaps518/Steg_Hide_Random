package com.stegrandom.Model;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class SteganographyImage {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final int totalPixels;
    private final Map<String, BitSet> channelPositions;

    public SteganographyImage(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.totalPixels = width * height;
        this.channelPositions = new HashMap<>();
        initializeChannelPositions();
    }

    private void initializeChannelPositions() {
        channelPositions.put("blue", new BitSet(totalPixels));
        channelPositions.put("red", new BitSet(totalPixels));
        channelPositions.put("green", new BitSet(totalPixels));
    }

    // Core pixel manipulation methods
    public int getRGB(int x, int y) {
        return image.getRGB(x, y);
    }

    public void setRGB(int x, int y, int rgb) {
        image.getRGB(x, y); // Optional: Add error checking for valid coordinates
        image.setRGB(x, y, rgb);
    }

    // Coordinate conversion utilities
    public int[] positionToCoordinates(int position) {
        int x = position % width;
        int y = position / width;
        return new int[]{x, y};
    }

    // Channel tracking methods
    public BitSet getChannelPositions(String channel) {
        return channelPositions.get(channel);
    }

    public void markPositionUsed(String channel, int position) {
        BitSet channelBits = channelPositions.get(channel);
        if (channelBits != null) {
            channelBits.set(position);
        }
    }

    public boolean isPositionUsed(String channel, int position) {
        BitSet channelBits = channelPositions.get(channel);
        return channelBits != null && channelBits.get(position);
    }

    // Color handling methods
    public Map<String, Integer> extractColorsFromRGB(int rgb) {
        return Map.of(
                "alpha", (rgb >> 24) & 255,
                "red", (rgb >> 16) & 255,
                "green", (rgb >> 8) & 255,
                "blue", rgb & 255
        );
    }

    public int reconstructRGB(Map<String, Integer> colors, String modifiedChannel, int modifiedValue) {
        Map<String, Integer> updatedColors = new HashMap<>(colors);
        updatedColors.put(modifiedChannel, modifiedValue);

        return (updatedColors.get("alpha") << 24) |
                (updatedColors.get("red") << 16) |
                (updatedColors.get("green") << 8) |
                updatedColors.get("blue");
    }

    public int insertBitIntoColor(int bit, int color) {
        return (color & 0xFE) | bit; // Clear LSB and set it to our bit
    }

    // Channel selection logic
    public String selectChannel(int bitIndex) {
        if (bitIndex < totalPixels) {
            return "blue";
        } else if (bitIndex < (totalPixels * 2)) {
            return "red";
        } else {
            return "green";
        }
    }

    // Getters
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

    // Capacity checking
    public boolean canFitMessage(int messageBitsLength) {
        return messageBitsLength <= (totalPixels * 3);  // 3 for B,R,G channels
    }
}