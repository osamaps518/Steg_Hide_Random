package com.stegrandom.utilites;

public class Utils {
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
}
