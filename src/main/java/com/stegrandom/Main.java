package com.stegrandom;

import com.stegrandom.Model.SteganographyImage;
import com.stegrandom.steganography.Steganography;
import com.stegrandom.utilites.Utils;

//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.Random;

//public class Main {

//    public static void main(String[] args) throws IOException {
//        // Using Random
//        Random rand1 = new Random(12345);
//        Random rand2 = new Random(12345);
//        // System.out.println(rand1.nextInt(100) == rand2.nextInt(100)); // true - same numbers
//
//        String input = "This is the secret message";
//
//        // Convert the input string to bits
//        StringBuilder bits = Utils.convertStringToBits(input);
//        System.out.println("Bits: " + bits);
//
//        // Convert the bits back to string
//        String output = Utils.convertBitsToString(bits);
//        System.out.println("Converted back to String: " + output);
//
//        // Verify if the output matches the input
//        if (input.equals(output)) {
//            System.out.println("Success! The output matches the original input.");
//        } else {
//            System.out.println("Error: The output does not match the original input.");
//        }
//    }
//}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Main <input_image> <secret_message>");
            System.out.println("Example: java Main image.png \"Hello World\"");
            System.exit(1);
        }

        try {
            // Read input image
            String inputImagePath = args[0];
            BufferedImage originalImage = ImageIO.read(new File(inputImagePath));

            // Get secret message (combine all remaining args to handle spaces)
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) messageBuilder.append(" ");
                messageBuilder.append(args[i]);
            }
            String secretMessage = messageBuilder.toString();

            // Create steganography image
            SteganographyImage stegImage = new SteganographyImage(originalImage);

            // Hide message
            System.out.println("Hiding message: " + secretMessage);
            BufferedImage modifiedImage = Steganography.hideMessage(stegImage, secretMessage);

            // Save modified image
            String outputPath = "steg_output.png";
            ImageIO.write(modifiedImage, "PNG", new File(outputPath));
            System.out.println("Modified image saved as: " + outputPath);

            // Verify by extracting
            SteganographyImage extractionImage = new SteganographyImage(modifiedImage);
            StringBuilder messageBits = Utils.convertStringToBits(secretMessage);
            String extractedMessage = Steganography.extractMessage(extractionImage, messageBits.length());
            System.out.println("Extracted message: " + extractedMessage);
            System.out.println("Message successfully hidden and verified!");

        } catch (IOException e) {
            System.err.println("Error reading/writing image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error processing message: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}