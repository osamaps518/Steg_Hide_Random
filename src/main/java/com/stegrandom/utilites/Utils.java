package com.stegrandom.utilites;

public class Utils {

    //To Convert a String into a sequence of bits
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
