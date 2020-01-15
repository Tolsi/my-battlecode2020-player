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
        if (message instanceof MSoupLocations) {
            MSoupLocations.write((MSoupLocations) message, bytes, 0);
        } else if (message instanceof MMapUpdates) {
            MMapUpdates.write((MMapUpdates) message, bytes, 0);
        }
    }

    static MMessage readMessage(byte[] bytes) {
        switch (bytes[0]) {
            case 0:
                return MSoupLocations.read(bytes, 0);
            case 1:
                return MMapUpdates.read(bytes, 0);
//            case 1:
//                break;
            default:
                return null;
        }
    }

    static int[] messageToTXData(MMessage m) {
        byte[] bytes = new byte[28];
        // keep it for CRC
        bytes[27] = 0;
        writeMessage(m, bytes);
        addCRC(bytes);
        return MUtil.bytesToIntArray(bytes);
    }


    static MMessage messageFromTXData(int[] data) {
        if (data.length != 7) {
            return null;
        }
        byte[] bytes = MUtil.intArrayToBytes(data);
        if (checkCRC(bytes)) {
            bytes[27] = 0;
            return readMessage(bytes);
        }
        return null;
    }

    static byte UNSIGNED_BYTE_MIN_VALUE = Byte.MIN_VALUE;
    static int UNSIGNED_BYTE_MAX_VALUE = 256;

    static byte saveSoupAsByte(int value) {
        // note: implicit rounding by 10! to put more values in a byte
        // 128 - see intAsUnsignedByte
        int newValue = (value / 10) - 128;
        if (newValue > Byte.MAX_VALUE) {
            newValue = Byte.MAX_VALUE;
        }
        return (byte) newValue;
    }

    static int readSoupFromByte(byte value) {
        if (value == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
            return 0;
        }
        return (value + 128) * 10;
    }

    static int bestFee(byte value) {
        if (value == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
            return 0;
        }
        return (value + 128) * 10;
    }

    static int goodEnoughtFee(byte value) {
        if (value == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
            return 0;
        }
        return (value + 128) * 10;
    }
}
