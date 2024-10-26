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
        // System.out.println(rand1.nextInt(100) == rand2.nextInt(100)); // true - same numbers

        String input = "This is the secret message";

        // Convert the input string to bits
        StringBuilder bits = Utils.convertStringToBits(input);
        System.out.println("Bits: " + bits);

        // Convert the bits back to string
        String output = Utils.convertBitsToString(bits);
        System.out.println("Converted back to String: " + output);

        // Verify if the output matches the input
        if (input.equals(output)) {
            System.out.println("Success! The output matches the original input.");
        } else {
            System.out.println("Error: The output does not match the original input.");
        }
    }
}
