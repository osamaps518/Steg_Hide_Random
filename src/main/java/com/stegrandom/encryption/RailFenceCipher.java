package com.stegrandom.encryption;


import java.util.Scanner;


/**
 * Implementation of the Rail Fence Cipher for message encryption/decryption.
 * Includes depth validation and handles edge cases.
 */
public class RailFenceCipher {

    /**
     * Main method for quick testing of the Rail Fence Cipher
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Rail Fence Cipher Test ===");
            while (true) {
                System.out.print("\nEnter 'e' for encrypt, 'd' for decrypt, or 'q' to quit: ");
                String choice = scanner.nextLine().toLowerCase();

                if (choice.equals("q")) {
                    break;
                }

                if (choice.equals("e")) {
                    System.out.print("Enter text to encrypt: ");
                    String plainText = scanner.nextLine();
                    System.out.print("Enter rail depth (2-10): ");
                    int depth = Integer.parseInt(scanner.nextLine());

                    String encrypted = encrypt(plainText, depth);
                    System.out.println("Encrypted text: " + encrypted);

                } else if (choice.equals("d")) {
                    System.out.print("Enter text to decrypt: ");
                    String cipherText = scanner.nextLine();
                    System.out.print("Enter rail depth (2-10): ");
                    int depth = Integer.parseInt(scanner.nextLine());

                    String decrypted = decrypt(cipherText, depth);
                    System.out.println("Decrypted text: " + decrypted);

                } else {
                    System.out.println("Invalid choice! Please enter 'e', 'd', or 'q'");
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    /**
     * Validates and adjusts the depth parameter to ensure effective encryption.
     * Maximum depth is capped at min(text.length()/2, 10) to ensure each rail
     * has sufficient characters for effective scrambling.
     *
     * @param depth      Requested depth
     * @param textLength Length of text to be encrypted
     * @return Validated depth value
     * @throws IllegalArgumentException if depth is less than 2
     */
    private static int validateDepth(int depth, int textLength) {
        if (depth < 2) {
            throw new IllegalArgumentException("Depth must be at least 2");
        }

        // Cap maximum depth
        int maxDepth = Math.min(textLength / 2, 10);
        return Math.min(depth, maxDepth);
    }

    /**
     * Encrypts a plaintext message using the Rail Fence Cipher.
     *
     * @param plainText Text to encrypt
     * @param depth     Number of rails (adjusted if too large)
     * @return Encrypted text
     * @throws IllegalArgumentException if depth < 2 or text is empty
     */
    public static String encrypt(String plainText, int depth) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text cannot be null or empty");
        }


        // Convert to uppercase but preserve spaces by replacing them with underscore
        plainText = plainText.replaceAll("[^A-Za-z0-9 ]", "").toUpperCase()
                .replaceAll(" ", "_");

        if (plainText.isEmpty()) {
            throw new IllegalArgumentException("Plain text contains no valid characters");
        }


        // Validate and adjust depth if necessary
        depth = validateDepth(depth, plainText.length());

        // Create the rail fence matrix
        char[][] fence = new char[depth][plainText.length()];
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < plainText.length(); j++) {
                fence[i][j] = ' ';
            }
        }

        // Fill the matrix in zigzag pattern
        int row = 0;
        int direction = 1;

        for (int col = 0; col < plainText.length(); col++) {
            fence[row][col] = plainText.charAt(col);

            if (row == 0) {
                direction = 1;
            } else if (row == depth - 1) {
                direction = -1;
            }

            row += direction;
        }

        // Read off the cipher text
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < plainText.length(); j++) {
                if (fence[i][j] != ' ') {
                    cipherText.append(fence[i][j]);
                }
            }
        }

        return cipherText.toString();
    }

    /**
     * Decrypts a Rail Fence encrypted message.
     *
     * @param cipherText Text to decrypt
     * @param depth      Number of rails (adjusted if too large)
     * @return Decrypted text
     * @throws IllegalArgumentException if depth < 2 or text is empty
     */
    public static String decrypt(String cipherText, int depth) {
        if (cipherText == null || cipherText.isEmpty()) {
            throw new IllegalArgumentException("Cipher text cannot be null or empty");
        }

        // Remove any spaces and validate
        // Don't remove underscores during decryption
        cipherText = cipherText.replaceAll("[^A-Za-z0-9_]", "");
        if (cipherText.isEmpty()) {
            throw new IllegalArgumentException("Cipher text contains no valid characters");
        }

        // Validate and adjust depth if necessary
        depth = validateDepth(depth, cipherText.length());

        // Create the rail fence matrix
        char[][] fence = new char[depth][cipherText.length()];
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < cipherText.length(); j++) {
                fence[i][j] = ' ';
            }
        }

        // Mark the positions in zigzag pattern
        int row = 0;
        int direction = 1;
        for (int col = 0; col < cipherText.length(); col++) {
            fence[row][col] = '*';

            if (row == 0) {
                direction = 1;
            } else if (row == depth - 1) {
                direction = -1;
            }

            row += direction;
        }

        // Fill the marked positions with cipher text
        int textIndex = 0;
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < cipherText.length(); j++) {
                if (fence[i][j] == '*') {
                    fence[i][j] = cipherText.charAt(textIndex++);
                }
            }
        }

        // Read off in zigzag pattern
        StringBuilder plainText = new StringBuilder();
        row = 0;
        direction = 1;

        for (int col = 0; col < cipherText.length(); col++) {
            plainText.append(fence[row][col]);

            if (row == 0) {
                direction = 1;
            } else if (row == depth - 1) {
                direction = -1;
            }

            row += direction;
        }

        String decrypted = plainText.toString();
        // Convert underscores back to spaces
        return decrypted.replaceAll("_", " ");
    }
}