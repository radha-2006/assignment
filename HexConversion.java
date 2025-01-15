import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HexConversion {

    // Convert hex to ASCII
    public static String hexToAscii(String hexString) {
        StringBuilder asciiString = new StringBuilder();
        for (int i = 0; i < hexString.length(); i += 2) {
            String hexPair = hexString.substring(i, i + 2);
            int charCode = Integer.parseInt(hexPair, 16);
            asciiString.append((char) charCode);
        }
        return asciiString.toString();
    }

    // Convert ASCII to hex
    public static String asciiToHex(String asciiString) {
        StringBuilder hexString = new StringBuilder();
        for (char ch : asciiString.toCharArray()) {
            hexString.append(String.format("%02x", (int) ch));  // Fixed format string for hex conversion
        }
        return hexString.toString();
    }

    // Convert hex to unknown (Base64 encoding of 32-man chunks)
    public static String hexToUnknown(String hexString) {
        StringBuilder unknownString = new StringBuilder();
        int chunkSize = 32;  // Each chunk represents 16 bytes (32 hex characters)
        for (int i = 0; i < hexString.length(); i += chunkSize) {
            int end = Math.min(i + chunkSize, hexString.length());
            String hexChunk = hexString.substring(i, end);

            // Convert hex chunk to byte array
            byte[] bytes = hexStringToByteArray(hexChunk);

            // Encode the byte array to Base64
            String base64Chunk = Base64.getEncoder().encodeToString(bytes);

            unknownString.append(base64Chunk);
        }
        return unknownString.toString();
    }

    // Convert unknown to hex (Base64 decoding back to hex)
    public static String unknownToHex(String unknownString) {
        StringBuilder hexString = new StringBuilder();

        // Decode the Base64 string back into bytes
        byte[] bytes = Base64.getDecoder().decode(unknownString);

        // Convert bytes to hex representation
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));  // Fixed format string for hex conversion
        }

        return hexString.toString();
    }

    // Helper function to convert hex to byte array
    private static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }

    // Method to read hex string from a JSON file
    public static String readHexFromJsonFile(String fileName) {
        String hexString = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(fileName));
            
            // Assuming the JSON structure contains the hex string under the "hex" field
            JsonNode hexNode = rootNode.path("hex"); // Adjust field name as necessary
            if (!hexNode.isMissingNode()) {
                hexString = hexNode.asText(); // Extract the hex string
            } else {
                System.out.println("Hex field not found in JSON file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static void main(String[] args) {
        // Read the hex input string from a JSON file
        String hexInput = readHexFromJsonFile("input.json");  // Adjust file name accordingly

        if (hexInput.isEmpty()) {
            System.out.println("No hex data found in the file.");
            return;
        }

        // Convert hex to unknown (Base64 encoding of chunks)
        String unknownString = hexToUnknown(hexInput);
        System.out.println("Unknown String: " + unknownString);

        // Convert unknown back to hex
        String hexOutput = unknownToHex(unknownString);
        System.out.println("Converted Back to Hex: " + hexOutput);

        // Convert hex to ASCII
        String asciiOutput = hexToAscii(hexInput);
        System.out.println("ASCII Output: " + asciiOutput);

        // Convert ASCII back to hex
        String hexFromAscii = asciiToHex(asciiOutput);
        System.out.println("Converted Back to Hex from ASCII: " + hexFromAscii);
    }
}
