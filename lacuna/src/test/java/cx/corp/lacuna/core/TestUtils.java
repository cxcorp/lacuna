package cx.corp.lacuna.core;

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
}
