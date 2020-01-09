package mybot;

public class MUtil {
    //region Ints to Bytes and back
    static int[] bytesToIntArray(byte[] array) {
        assert array.length == 28;
        // keep it for CRC
        array[27] = 0;

        int[] result = new int[7];

        for (int i = 0; i < array.length; i += 4) {
            result[i] = array[i + 3] & 0xFF |
                    (array[i + 2] & 0xFF) << 8 |
                    (array[i + 1] & 0xFF) << 16 |
                    (array[i] & 0xFF) << 24;
        }

        return result;
    }


    static byte[] intArrayToBytes(int[] array) {
        assert array.length == 7;

        byte[] result = new byte[28];

        for (int i = 0; i < array.length; i++) {
            int in = array[i];
            result[i + 3] = (byte) (in & 0xFF);
            result[i + 2] = (byte) ((in >> 8) & 0xFF);
            result[i + 1] = (byte) ((in >> 16) & 0xFF);
            result[i] = (byte) ((in >> 24) & 0xFF);
        }

        return result;
    }

    static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    static byte[] intToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }
    //endregion
}
