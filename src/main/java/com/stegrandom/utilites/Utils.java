package com.stegrandom.utilites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The type Utils.
 */
public class Utils {
    /**
     * Convert string to bits string builder.
     *
     * @param input the input
     * @return the string builder
     */
    public static StringBuilder convertStringToBits(String input){
        byte[] bytes = input.getBytes();
        StringBuilder bits = new StringBuilder();

        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                // Bit masking
                bits.append((b >> i) & 1);
            }
        }
        return bits;
    }

    /**
     * Embid bit in pixel.
     *
     * @param pixel the pixel
     * @param bit   the bit
     */
    public static void embidBitInPixel(int[] pixel, int bit){
    }


    /**
     * Read image buffered image.
     *
     * @param path the path
     * @return the buffered image
     * @throws IOException the io exception
     */
    public static BufferedImage readImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        return image;
    }
}
