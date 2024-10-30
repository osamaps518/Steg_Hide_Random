package com.stegrandom.utilites;

import java.nio.charset.StandardCharsets;

/**
 * Utility class providing core functionality for steganographic operations,
 * primarily focused on bit manipulation and string conversion operations.
 * This class contains static methods for converting between strings and their
 * binary representations, as well as utility methods for character processing.
 */
public class Utils {

    /**
     * Converts a string input into its binary representation using UTF-8 encoding.
     * Each character in the input string is converted to its 8-bit binary representation,
     * with the bits arranged in big-endian order (most significant bit first).
     *
     * @param input The string to be converted to binary. Must not be null or empty.
     * @return A StringBuilder containing the binary representation of the input string,
     *         where each character is represented by 8 bits, or null if the input is
     *         null or empty.
     *
     * @example
     * <pre>
     * String input = "A";
     * StringBuilder result = Utils.convertStringToBits(input);
     * // result contains "01000001" (ASCII/UTF-8 binary for 'A')
     * </pre>
     */
    public static StringBuilder convertStringToBits(String input) {
        if(input == null || input.isEmpty()) {
            return null;
        }
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        StringBuilder bits = new StringBuilder();

        for (byte b : bytes) {
            for (int i = 7; i >= 0; i--) {
                // Bit masking: right-shift byte by i positions and mask with 1
                // to extract each bit from most to least significant
                bits.append((b >> i) & 1);
            }
        }
        return bits;
    }

    /**
     * Converts a character representing a numerical digit to its corresponding integer value.
     * This method performs direct ASCII arithmetic to convert a character digit to its
     * numeric value by subtracting the ASCII value of '0'.
     *
     * @param c The character to convert. Must be a numeric character ('0' to '9').
     * @return The integer value represented by the character.
     * @throws IllegalArgumentException if the character is not a valid digit (0-9).
     *
     * @example
     * <pre>
     * int digit = Utils.charToDigit('5'); // Returns 5
     * </pre>
     */
    public static int charToDigit(char c) {
        if (c < '0' || c > '9') {
            throw new IllegalArgumentException("Character must be a digit between 0 and 9");
        }
        return c - '0';
    }

    /**
     * Converts a binary sequence back into its original string representation.
     * This method processes the binary data in 8-bit chunks, converting each chunk
     * into its corresponding character using UTF-8 encoding.
     *
     * @param bits StringBuilder containing the binary sequence. The length must be
     *             a multiple of 8 as each character requires 8 bits.
     * @return The decoded string from the binary sequence.
     * @throws IndexOutOfBoundsException if the bits length is not a multiple of 8
     *         or if accessing an invalid position in the StringBuilder.
     *
     * @example
     * <pre>
     * StringBuilder bits = new StringBuilder("01000001"); // Binary for 'A'
     * String result = Utils.convertBitsToString(bits); // Returns "A"
     * </pre>
     */
    public static String convertBitsToString(StringBuilder bits) {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < bits.length(); i += 8) {
            int charCode = 0;

            // Convert each 8-bit sequence to a character
            for (int j = 0; j < 8; j++) {
                // Left shift existing bits and add new bit
                charCode = (charCode << 1) | (bits.charAt(i + j) - '0');
            }
            message.append((char) charCode);
        }

        return message.toString();
    }
}