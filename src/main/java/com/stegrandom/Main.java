package com.stegrandom;

import com.stegrandom.utilites.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        // Using Random
        Random rand1 = new Random(12345);
        Random rand2 = new Random(12345);
//        System.out.println(rand1.nextInt(100) == rand2.nextInt(100)); // true - same numbers

        String input = "This is the secret message";
        System.out.println(Utils.convertStringToBits(input));

        //BufferedImage image = Utils.readImage(args[0]);
        //System.out.println(image.getRGB(0,0));

        System.out.println(Utils.convertBitsToString(input));
    }
}