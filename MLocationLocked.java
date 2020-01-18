package mybot;

import battlecode.common.MapLocation;

public class MLocationLocked extends MMessage {
    MapLocation location;

    public MLocationLocked(MapLocation location) {
        assert location != null;
        this.location = location;
    }

    static MLocationLocked read(byte[] bytes, int from) {
        // 0 byte is header
        from ++;
        return new MLocationLocked(new MapLocation(bytes[from++], bytes[from]));
    }

    static byte header = 2;

    static boolean write(MLocationLocked l, byte[] bytes, int from) {
        bytes[from++] = header;
        bytes[from++] = (byte)l.location.x;
        bytes[from++] = (byte)l.location.y;
        return from <= 28;
    }
}
