package com.stegrandom;

import com.stegrandom.utilites.Utils;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Using Random
        Random rand1 = new Random(12345);
        Random rand2 = new Random(12345);
        System.out.println(rand1.nextInt(100) == rand2.nextInt(100)); // true - same numbers

        String input = "This is the secret message";
        System.out.println(Utils.convertStringToBits(input));
    }
}