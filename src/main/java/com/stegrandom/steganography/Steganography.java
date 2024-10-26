package com.stegrandom.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class Steganography {

   public static BufferedImage hideMessage(BufferedImage inputImage, String secretMsg){
       int imageHeight = inputImage.getHeight();
       int imageWidth = inputImage.getWidth();


       for(int y = 0; y < imageHeight; y++){
           for(int x = 0; x < imageWidth; x++){
              int rgb = inputImage.getRGB(x, y);
              Map colors = extractColorsFromRGB(rgb);
           }
       }
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
