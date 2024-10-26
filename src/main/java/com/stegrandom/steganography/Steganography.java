package com.stegrandom.steganography;

import com.stegrandom.utilites.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Steganography {

   public static BufferedImage hideMessage(BufferedImage inputImage, String secretMsg){
       int imageHeight = inputImage.getHeight();
       int imageWidth = inputImage.getWidth();

       // Convert the message to a binary string
       StringBuilder messageBits = Utils.convertStringToBits(secretMsg);
       int messageLength = messageBits.length();

       // Set up a random distribution
       Random random = new Random(12345);  // Seed for reproducibility
       int bitIndex = 0;

       // Loop over the image pixels
       while (bitIndex < messageLength) {
           int x = random.nextInt(imageWidth);
           int y = random.nextInt(imageHeight);

           int rgb = inputImage.getRGB(x, y);
           Map<String, Integer> colors = extractColorsFromRGB(rgb);

           // Modify the RGB values based on the message bits
           int red = (colors.get("red") & 0xFE) | (messageBits.charAt(bitIndex++) - '0');
           int green = (bitIndex < messageLength) ? (colors.get("green") & 0xFE) | (messageBits.charAt(bitIndex++) - '0') : colors.get("green");
           int blue = (bitIndex < messageLength) ? (colors.get("blue") & 0xFE) | (messageBits.charAt(bitIndex++) - '0') : colors.get("blue");

           // Reconstruct the RGB integer with modified colors
           int modifiedRGB = (colors.get("alpha") << 24) | (red << 16) | (green << 8) | blue;

           // Update the image with the modified RGB value
           inputImage.setRGB(x, y, modifiedRGB);
       }

       return inputImage;
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
