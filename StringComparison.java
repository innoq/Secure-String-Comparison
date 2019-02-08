import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class StringComparison {

    public static boolean stringEquals(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }


    public static boolean stringEqualsNoShortcut(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        }
        boolean isEqual = true;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                isEqual = false;
            }
        }
        return isEqual;
    }

    public static boolean stringHashEquals(String s1, String s2)  {
        if (s1 == null || s2 == null) {
            return false;
        }

        MessageDigest sha256 = getSHA256Digest();

        byte[] s1AsByteArray = s1.getBytes(StandardCharsets.UTF_8);
        byte[] s2AsByteArray = s2.getBytes(StandardCharsets.UTF_8);

        byte[] sha256S1 = sha256.digest(s1AsByteArray);
        byte[] sha256S2 = sha256.digest(s2AsByteArray);

        String sha256S1AsHexString = bytesToHexString(sha256S1);
        String sha256S2AsHexString = bytesToHexString(sha256S2);

        return stringEquals(sha256S1AsHexString, sha256S2AsHexString);
    }

    public static boolean stringRnddHashEquals(String s1, String s2)  {
        if (s1 == null || s2 == null) {
            return false;
        }

        MessageDigest sha256 = getSHA256Digest();

        byte[] randomBytes = getRandomBytes(16);

        byte[] s1AsByteArray = s1.getBytes(StandardCharsets.UTF_8);
        byte[] s2AsByteArray = s2.getBytes(StandardCharsets.UTF_8);

        byte[] rndS1 = appendByteArray(randomBytes, s1AsByteArray);
        byte[] rndS2 = appendByteArray(randomBytes, s2AsByteArray);

        byte[] rndSha256S1 = sha256.digest(rndS1);
        byte[] rndsha256S2 = sha256.digest(rndS2);

        String sha256S1AsHexString = bytesToHexString(rndSha256S1);
        String sha256S2AsHexString = bytesToHexString(rndsha256S2);

        return stringEquals(sha256S1AsHexString, sha256S2AsHexString);

    }

    public static boolean stringHashPlusRandomConstantTimeEquals(String s1, String s2)  {
        if (s1 == null || s2 == null) {
            return false;
        }

        MessageDigest sha256 = getSHA256Digest();

        byte[] randomBytes = getRandomBytes(16);

        byte[] s1AsByteArray = s1.getBytes(StandardCharsets.UTF_8);
        byte[] s2AsByteArray = s2.getBytes(StandardCharsets.UTF_8);

        byte[] rndS1 = appendByteArray(randomBytes, s1AsByteArray);
        byte[] rndS2 = appendByteArray(randomBytes, s2AsByteArray);

        byte[] rndSha256S1 = sha256.digest(rndS1);
        byte[] rndsha256S2 = sha256.digest(rndS2);

        String sha256S1AsHexString = bytesToHexString(rndSha256S1);
        String sha256S2AsHexString = bytesToHexString(rndsha256S2);

        return stringEqualsNoShortcut(sha256S1AsHexString, sha256S2AsHexString);
    }

    private static byte[] getRandomBytes(int n) {
        try {
            SecureRandom random =  SecureRandom.getInstanceStrong();
            byte[] randomBytes = new byte[n];
            random.nextBytes(randomBytes);
            return randomBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] appendByteArray(byte[] b1, byte[] b2) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            stream.write(b1);
            stream.write(b2);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static MessageDigest getSHA256Digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
