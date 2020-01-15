package mybot;

import battlecode.common.MapLocation;

public class MSoupLocation {
    MapLocation location;
    byte value;

    public MSoupLocation(MapLocation location, byte value) {
        this.location = location;
        this.value = value;
    }

    static MSoupLocation read(byte[] bytes, int from) {
        // 0 byte is header
        return new MSoupLocation(new MapLocation(bytes[from++], bytes[from++]), bytes[from]);
    }

    static int size = 3;

    static void write(MSoupLocation l, byte[] bytes, int from) {
        bytes[from++] = (byte) l.location.x;
        bytes[from++] = (byte) l.location.y;
        bytes[from] = l.value;
    }

}
