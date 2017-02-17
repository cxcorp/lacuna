package cx.corp.lacuna.core;

import java.util.concurrent.ThreadLocalRandom;

public final class TestUtils {

    // O(n^2), don't care enough
    public static boolean containsAll(int[] array, int[] expectedElems) {
        for (int val : expectedElems) {
            if (!contains(array, val)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(int[] array, int value) {
        for (int val : array) {
            if (val == value) {
                return true;
            }
        }
        return false;
    }

    public static byte[] generateRandomBytes(int count) {
        byte[] buf = new byte[count];
        ThreadLocalRandom.current().nextBytes(buf);
        return buf;
    }
}
