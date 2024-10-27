package com.stegrandom.utilites;

import java.nio.charset.StandardCharsets;

public class Utils {
    public static StringBuilder convertStringToBits(String input){
        if(input == null || input.isEmpty()){
            return null;
        }
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
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
     * Char to digit int.
     * Change the format from char to digit
     * this method will throw an exception if the char provided is not a digit 0-9
     *
     * @param c the c
     * @return the int
     */
    public static int charToDigit(char c) {
        // Subtracts the Ascii value of c from 0 and you get c in int instead of char
        return c - '0';
    }

    // Converts a bit sequence back into a String (for decoding)
    public static String convertBitsToString(StringBuilder bits) {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < bits.length(); i += 8) {
            int charCode = 0;

            // Collect each byte (8 bits) and convert to a character
            for (int j = 0; j < 8; j++) {
                charCode = (charCode << 1) | (bits.charAt(i + j) - '0');
            }
            message.append((char) charCode);
        }

        return message.toString();
    }
}
