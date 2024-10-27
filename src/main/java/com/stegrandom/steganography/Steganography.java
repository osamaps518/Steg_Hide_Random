package com.stegrandom.steganography;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.utilites.Utils;

import java.awt.image.BufferedImage;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The type Steganography.
 */
//public class Steganography {
//private static final Map<String, BitSet> channelPositions = new HashMap<>();
//
//    // Select appropriate BitSet based on current channel
//    private static BitSet selectBitSet(String colorSelected) {
//        return channelPositions.get(colorSelected);
//    }
//    // must change totalPixels to long later when you implement other random generation algorithm
//    private static int getAndMarkRandomPosition(Random random, BitSet channelPositions, int totalPixels) {
//        int position;
//        do {
//            position = random.nextInt(totalPixels);
//        } while (channelPositions.get(position)); // Keep trying until we find unused position
//
//        channelPositions.set(position); // Mark as used
//        return position;
//    }
//    public static BufferedImage hideMessage(SteganographyImage image, String secretMsg) {
//        StringBuilder messageBits = Utils.convertStringToBits(secretMsg);
//        int messageLength = messageBits.length();
//
//        // Initialize MT19937 with seed
//        Random random = new Random(12345);
//
//        // We need three BitSets, one for each color channel
//        channelPositions.put("blue", new BitSet((int)image.getTotalPixels()));
//        channelPositions.put("red", new BitSet((int)image.getTotalPixels()));
//        channelPositions.put("green", new BitSet((int)image.getTotalPixels()));
//
//
//        int bitIndex = 0;
//        while (bitIndex < messageLength) {
//            String colorSelected = selectColor(image.getTotalPixels(), bitIndex);
//            BitSet currentChannelPositions = selectBitSet(colorSelected);
//            int position = getAndMarkRandomPosition(random, currentChannelPositions, (int)image.getTotalPixels());
//
////        Positions:
////        0  1  2  3
////        4  5  6  7
////        8  9 10 11
////
////        (x,y) coordinates:
////        (0,0) (1,0) (2,0) (3,0)
////        (0,1) (1,1) (2,1) (3,1)
////        (0,2) (1,2) (2,2) (3,2)
//            // Convert position to x,y coordinates
//            int x = (int)(position % image.getWidth());
//            int y = (int)(position / image.getWidth());
//
//            int rgb = image.getImage().getRGB(x, y);
//            Map<String, Integer> colors = extractColorsFromRGB(rgb);
//
//            int currentBit = Utils.charToDigit(messageBits.charAt(bitIndex));
//            int selectedColorAfterModification = insertBitIntoColor(currentBit, colors.get(colorSelected));
//            int modifiedRGB = reconstructRGB(
//                    colors.get("alpha"),
//                    colors.get("red"),
//                    colors.get("green"),
//                    colors.get("blue"),
//                    selectedColorAfterModification,
//                    colorSelected
//            );
//
//            image.getImage().setRGB(x, y, modifiedRGB);
//            bitIndex++;
//        }
//        return image.getImage();
//    }
//
//    /**
//     * Select color string.
//     * Priority is Blue, if exhausted then red, else green
//     *
//     * @param totalPixels the total pixels
//     * @param bitIndex    the bit index
//     * @return the string
//     */
//    public static String selectColor(long totalPixels, int bitIndex) {
//        if (bitIndex < totalPixels) {
//            return "blue";    // Use all blue channels first
//        } else if (bitIndex < (totalPixels * 2)) {
//            return "red";     // After blues are used, use all reds
//        } else {
//            return "green";   // After blues and reds, use all greens
//        }
//    }
//
//
//    /**
//     * Reconstruct rgb int.
//     *
//     * @param alpha         the alpha
//     * @param red           the red
//     * @param green         the green
//     * @param blue          the blue
//     * @param modifiedColor the modified color
//     * @param colorSelected the color selected
//     * @return the int
//     */
//    public static int reconstructRGB(int alpha, int red, int green, int blue, int modifiedColor, String colorSelected){
//        if(colorSelected.equals("blue")){
//            return assembleRGB(alpha, red, green, modifiedColor);
//        }
//        else if(colorSelected.equals("red")){
//            return assembleRGB(alpha, modifiedColor, green, blue);
//        }
//        else { // green
//            return assembleRGB(alpha, red, modifiedColor, blue);
//        }
//    }
//
//    public static int assembleRGB(int alpha, int red, int green, int blue){
//        return alpha << 24 | red << 16 | green << 8 | blue;
//    }
//   public static int insertBitIntoColor(int bit, int color) {
//       // use 0xFE to clear the least significant bit "11111110"
//       return (color & 0xFE) | bit;
//   }
//
////    The RGB values you get will be packed into a single integer, where:
////
////    rgb       = AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB
////    0xff      = 00000000 00000000 00000000 11111111
////    rgb & 0xff = keeps only the last 8 bit
//   public static Map<String, Integer> extractColorsFromRGB(int rgb){
//       //    Bits 24-31: Alpha
//       int alpha = rgb >> 24 & 255;
//       //    Bits 16-23: Red
//       int red = rgb >> 16 & 255;
//       //    Bits 8-15: Green
//       int green = rgb >> 8 & 255;
//       //    Bits 0-7: Blue
//       int blue = rgb & 255;
//
//       Map<String, Integer> colors = Map.of(
//               "red", red,
//               "green", green,
//               "blue", blue,
//               "alpha", alpha
//       );
//       return colors;
//   }
////        ImageIO.write(image, "png", new File("output.png"));
//}

/**
 * The type Steganography.
 */
public class Steganography {

    // must change totalPixels to long later when you implement other random generation algorithm
    public static int getAndMarkRandomPosition(Random random, SteganographyImage image, String channel) {
        int position;
        do {
            position = random.nextInt(image.getTotalPixels());
        } while (image.isPositionUsed(channel, position));

        image.markPositionUsed(channel, position);
        return position;
    }

    public static BufferedImage hideMessage(SteganographyImage image, String secretMsg) {
        if (secretMsg == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        StringBuilder messageBits = Utils.convertStringToBits(secretMsg);
        if (messageBits == null) {
            throw new IllegalArgumentException("Cannot convert null or empty message to bits");
        }
        int messageLength = messageBits.length();

        // Validate message can fit in image
        if (!image.canFitMessage(messageLength)) {
            throw new IllegalArgumentException("Message too long for this image");
        }

        // Initialize MT19937 with seed
        Random random = new Random(12345);

        int bitIndex = 0;
        while (bitIndex < messageLength) {
            String colorSelected = image.selectChannel(bitIndex);
            // Get random unused position
            int position = getAndMarkRandomPosition(random, image, colorSelected);

            // Get coordinates and modify pixel
            int[] coordinates = image.positionToCoordinates(position);
            int x = coordinates[0];
            int y = coordinates[1];

            // Get current pixel color values
            int rgb = image.getRGB(x, y);
            Map<String, Integer> colors = image.extractColorsFromRGB(rgb);

            // Modify the selected color channel
            int currentBit = Utils.charToDigit(messageBits.charAt(bitIndex));
            int modifiedColor = image.insertBitIntoColor(currentBit, colors.get(colorSelected));

            // Reconstruct and set the modified pixel
            int modifiedRGB = image.reconstructRGB(colors, colorSelected, modifiedColor);
            image.setRGB(x, y, modifiedRGB);

            bitIndex++;
        }

        return image.getImage();
    }
    public static String extractMessage(SteganographyImage image, int messageLength) {
        if (messageLength <= 0) {
            throw new IllegalArgumentException("Message length must be positive");
        }

        // Validate message length can fit in image
        if (!image.canFitMessage(messageLength)) {
            throw new IllegalArgumentException("Specified message length is too large for this image");
        }

        // Initialize MT19937 with same seed
        Random random = new Random(12345);

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