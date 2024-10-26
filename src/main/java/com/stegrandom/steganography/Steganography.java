package com.stegrandom.steganography;

import com.stegrandom.utilites.Utils;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Random;

public class Steganography {

   public static BufferedImage hideMessage(BufferedImage inputImage, String secretMsg){
       int imageHeight = inputImage.getHeight();
       int imageWidth = inputImage.getWidth();
       int totalPixels = imageWidth * imageHeight;

       // Convert the message to a binary string
       StringBuilder messageBits = Utils.convertStringToBits(secretMsg);
       int messageLength = messageBits.length();

       if(!Utils.isImgSizeLongEnough(messageLength, imageHeight, messageLength)){
           throw new IllegalArgumentException("Image size is too short.");
       }

       // Set up a random distribution
       Random random = new Random(12345);  // Seed for reproducibility
       int bitIndex = 0;

       // Loop over the image pixels
       while (bitIndex < messageLength) {
           int x = random.nextInt(imageWidth);
           int y = random.nextInt(imageHeight);

           int rgb = inputImage.getRGB(x, y);
           Map<String, Integer> colors = extractColorsFromRGB(rgb);

           String colorSelected = selectColor(totalPixels, bitIndex);
           int currentBit = Utils.charToDigit(messageBits.charAt(bitIndex));
           int selectedColorAfterModification = insertBitIntoColor(currentBit, colors.get(colorSelected));
           int modifiedRGB = reconstructRGB(colors.get("alpha"), colors.get("red"), colors.get("green"), colors.get("blue"), selectedColorAfterModification, colorSelected);

           // Update the image with the modified RGB value
           inputImage.setRGB(x, y, modifiedRGB);
       }

       return inputImage;
   }

    /**
     * Select color string.
     *
     * @param totalPixels the total pixels
     * @param bitIndex    the bit index
     * @return the string
     */
    // This method logic needs review
    public static String selectColor(int totalPixels, int bitIndex){
       if(bitIndex < totalPixels){
           return "blue";
       }
       else if (bitIndex * 2 < totalPixels){
           return "red";
       }
       return "green";
   }


    /**
     * Reconstruct rgb int.
     *
     * @param alpha         the alpha
     * @param red           the red
     * @param green         the green
     * @param blue          the blue
     * @param modifiedColor the modified color
     * @param colorSelected the color selected
     * @return the int
     */
    public static int reconstructRGB(int alpha, int red, int green, int blue, int modifiedColor, String colorSelected){
        if(colorSelected.equals("blue")){
            return assembleRGB(alpha, red, green, modifiedColor);
        }
        else if(colorSelected.equals("red")){
            return assembleRGB(alpha, modifiedColor, green, blue);
        }
        else { // green
            return assembleRGB(alpha, red, modifiedColor, blue);
        }
    }

    public static int assembleRGB(int alpha, int red, int green, int blue){
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
   public static int insertBitIntoColor(int bit, int color) {
       // use 0xFE to clear the least significant bit "11111110"
       return (color & 0xFE) | bit;
   }

//    The RGB values you get will be packed into a single integer, where:
//
//    rgb       = AAAAAAAA RRRRRRRR GGGGGGGG BBBBBBBB
//    0xff      = 00000000 00000000 00000000 11111111
//    rgb & 0xff = keeps only the last 8 bit
   public static Map<String, Integer> extractColorsFromRGB(int rgb){
       //    Bits 24-31: Alpha
       int alpha = rgb >> 24 & 255;
       //    Bits 16-23: Red
       int red = rgb >> 16 & 255;
       //    Bits 8-15: Green
       int green = rgb >> 8 & 255;
       //    Bits 0-7: Blue
       int blue = rgb & 255;

       Map<String, Integer> colors = Map.of(
               "red", red,
               "green", green,
               "blue", blue,
               "alpha", alpha
       );
       return colors;
   }
//        ImageIO.write(image, "png", new File("output.png"));
}
