package mybot;

public class MUpdateSettings extends MMessage {
    String name;
    byte value;

    public MUpdateSettings(String name, byte value) {
        assert name != null;
        this.name = name;
        this.value = value;
    }

    static MUpdateSettings read(byte[] bytes, int from) {
        // 0 byte is header
        from ++;
        byte stringLen = bytes[from++];
        byte[] stringBytes = new byte[stringLen];
        System.arraycopy(bytes, from, stringBytes, 0, stringLen);
        from += stringBytes.length;
        String name = new String(stringBytes);
        return new MUpdateSettings(name, bytes[from]);
    }

    static byte header = 3;

    static boolean write(MUpdateSettings l, byte[] bytes, int from) {
        bytes[from++] = header;
        byte[] stringBytes = l.name.getBytes();
        bytes[from++] = (byte) stringBytes.length;
        System.arraycopy(stringBytes, 0, bytes, from, stringBytes.length);
        from += stringBytes.length;
        bytes[from++] = l.value;
        return from <= 28;
    }
}
