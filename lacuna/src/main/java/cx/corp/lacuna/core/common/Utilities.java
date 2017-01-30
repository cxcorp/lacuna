package cx.corp.lacuna.core.common;

public class Utilities {
    public static int[] copyToFittedArray(int[] array, int actualCount) {
        int[] fittedArray = new int[actualCount];
        System.arraycopy(array, 0, fittedArray, 0, actualCount);
        return fittedArray;
    }

    public static byte[] copyToFittedArray(byte[] array, int actualCount) {
        byte[] fittedArray = new byte[actualCount];
        System.arraycopy(array, 0, fittedArray, 0, actualCount);
        return fittedArray;
    }
}
