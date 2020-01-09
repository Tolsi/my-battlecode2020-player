package mybot;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import java.util.Objects;

// todo compress data using https://github.com/tomgibara/bits/blob/master/src/main/java/com/tomgibara/bits/BytesBitStore.java
public class MMapUpdate {
    MapLocation location;
    RobotType type;

    public MMapUpdate(MapLocation location, RobotType type) {
        this.location = location;
        this.type = type;
    }

    static MMapUpdate read(byte[] bytes, int from) {
        // 0 byte is header
        RobotType rt = null;
        if (bytes[from + 2] > 0) {
            rt = RobotType.values()[bytes[from + 2] - 1];
        }
//        Team t = null;
//        if (bytes[from + 3] > 0){
//            t = Team.values()[bytes[from + 3] - 1];
//        }
//        Operation op = null;
//        if (bytes[from + 4] > 0) {
//            op = Operation.values()[bytes[from + 4]];
//        }

        return new MMapUpdate(new MapLocation(bytes[from], bytes[from + 1]), rt);
    }

    static void write(MMapUpdate l, byte[] bytes, int from) {
        // 3-4 bits?
//        bytes[from] = header;
        // 6 bits
        bytes[from++] = (byte) l.location.x;
        // 6 bits
        bytes[from++] = (byte) l.location.y;
        // 4 bits
        if (l.type == null) {
            bytes[from] = (byte) 0;
        } else {
            bytes[from] = (byte) (l.type.ordinal() + 1);
        }
        // 2 bits
//        bytes[from + 4] = (byte) l.op.ordinal();
    }

    static int size = 3;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MMapUpdate that = (MMapUpdate) o;
        return location.equals(that.location) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, type);
    }
}
