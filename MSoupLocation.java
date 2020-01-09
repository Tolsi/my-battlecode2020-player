package mybot;

import battlecode.common.MapLocation;

public class MSoupLocation extends MMessage {
    MapLocation location;

    public MSoupLocation(MapLocation location) {
        this.location = location;
    }

    static byte header = 0;

    static MSoupLocation read(byte[] bytes, int from) {
        // 0 byte is header
        return new MSoupLocation(new MapLocation(bytes[from+1], bytes[from+2]));
    }

    static void write(MSoupLocation l, byte[] bytes, int from) {
        bytes[from] = header;
        bytes[from+1] = (byte) l.location.x;
        bytes[from+2] = (byte) l.location.y;
    }

}
