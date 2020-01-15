package mybot;

import java.util.LinkedList;
import java.util.List;

public class MSoupLocations extends MMessage {
    List<MSoupLocation> locations;

    public MSoupLocations(List<MSoupLocation> locations) {
        this.locations = locations;
    }

    static byte header = 0;

    static MSoupLocations read(byte[] bytes, int from) {
        // 0 byte is header
        List<MSoupLocation> locations = new LinkedList<>();
        from += 2;

        for (byte i = 0; i < bytes[1]; i++) {
            locations.add(MSoupLocation.read(bytes, from));
            from += MSoupLocation.size;
        }

        return new MSoupLocations(locations);
    }

    static void write(MSoupLocations l, byte[] bytes, int from) {
        bytes[from++] = header;
        bytes[from++] = (byte) l.locations.size();
        for (MSoupLocation loc : l.locations) {
            MSoupLocation.write(loc, bytes, from);
            from += MSoupLocation.size;
        }
    }

}
