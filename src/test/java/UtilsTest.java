import com.stegrandom.utilites.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.stream.Stream;

import static com.stegrandom.utilites.Utils.convertBitsToString;
import static com.stegrandom.utilites.Utils.convertStringToBits;
import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    // test convertStringToBits
    @Test
    @DisplayName("Test single ASCII character conversion")
    void testSingleCharacter() {
        String input = "A";  // ASCII 65 = 01000001
        String expected = "01000001";
        assertEquals(expected, convertStringToBits(input).toString());
    }

    @Test
    @DisplayName("Test basic English string conversion")
    void testBasicString() {
        String input = "Hi";  // H = 72 = 01001000, i = 105 = 01101001
        String expected = "0100100001101001";
        assertEquals(expected, convertStringToBits(input).toString());
    }

    @Test
    @DisplayName("Test string with special characters")
    void testSpecialCharacters() {
        String input = "!@#";
        StringBuilder result = convertStringToBits(input);
        assertNotNull(result);
        assertEquals(24, result.length(), "Each character should produce 8 bits");
    }

    @Test
    @DisplayName("Test UTF-8 characters (emoji)")
    void testUTF8Characters() {
        String input = "👋";  // This emoji is 4 bytes in UTF-8
        StringBuilder result = convertStringToBits(input);
        assertNotNull(result);
        assertEquals(32, result.length(), "4-byte UTF-8 character should produce 32 bits");
    }


    // Test convert Bits to String

    @Test
    @DisplayName("Convert bits representing letter 'A'")
    void testBasicConversion() {
        StringBuilder bits = new StringBuilder("01000001"); // Letter 'A'
        String result = convertBitsToString(bits);
        assertEquals("A", result);
    }

    @Test
    @DisplayName("Convert bits representing 'Hi'")
    void testMultipleCharacters() {
        // "Hi" in binary
        StringBuilder bits = new StringBuilder("0100100001101001");
        String result = convertBitsToString(bits);
        assertEquals("Hi", result);
    }

    @Test
    @DisplayName("Convert bits representing space character")
    void testSpaceCharacter() {
        StringBuilder bits = new StringBuilder("00100000"); // Space character
        String result = convertBitsToString(bits);
        assertEquals(" ", result);
    }

    @Test
    @DisplayName("Convert bits representing special characters")
    void testSpecialCharacter() {
        // "!@" in binary
        StringBuilder bits = new StringBuilder("0010000101000000");
        String result = convertBitsToString(bits);
        assertEquals("!@", result);
    }

    @Test
    @DisplayName("Convert empty bits sequence")
    void testEmptyBits() {
        StringBuilder bits = new StringBuilder("");
        String result = convertBitsToString(bits);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should throw exception for incomplete byte")
    void testIncompleteByte() {
        // Only 7 bits instead of 8
        StringBuilder bits = new StringBuilder("0100000");
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            convertBitsToString(bits);
        });
    }

    @Test
    @DisplayName("Should throw exception for partial last byte")
    void testPartialLastByte() {
        // 12 bits (1.5 bytes) instead of 16 bits (2 bytes)
        StringBuilder bits = new StringBuilder("010000010100");
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
           convertBitsToString(bits);
        });
    }

    @Test
    @DisplayName("Test round trip conversion: string -> bits -> string")
    void testRoundTrip() {
        String original = "Hello World!";
        StringBuilder bits = convertStringToBits(original);
        String result = convertBitsToString(bits);
        assertEquals(original, result);
    }
}
