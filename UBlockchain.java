package mybot;

public class UBlockchain {
    //region CRC
    static byte CRC_BYTE = 27;
    static byte checksum(byte[] bytes) {
        int checksum = 19283;
        for (int i = 0; i < CRC_BYTE; i++) {
            checksum += bytes[i] * (i - 82);
        }
        return (byte) ((checksum % 255) - 128);
    }
    static void addCRC(byte[] bytes) {
        // crc byte
        bytes[CRC_BYTE] = checksum(bytes);
    }
    static boolean checkCRC(byte[] bytes) {
        return checksum(bytes) == bytes[CRC_BYTE];
    }
    //endregion

    static void writeMessage(MMessage message, byte[] bytes) {
        if (message instanceof MMapUpdates) {
            MMapUpdates.write((MMapUpdates) message, bytes, 2);
        }
    }

    static MMessage readMessage(byte[] bytes) {
        switch (bytes[0]) {
            case 1:
                return MMapUpdates.read(bytes, 0);
//            case 1:
//                break;
            default:
                return null;
        }
    }

    static int[] msgToTXData(MMessage m) {
        byte[] bytes = new byte[28];
        writeMessage(m, bytes);
        addCRC(bytes);
        return MUtil.bytesToIntArray(bytes);
    }


    static MMessage msgFromTXData(int[] data) {
        byte[] bytes = MUtil.intArrayToBytes(data);
        if (checkCRC(bytes)) {
            return readMessage(bytes);
        }
        return null;
    }
}
