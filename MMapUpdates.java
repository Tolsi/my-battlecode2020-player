package mybot;

import battlecode.common.RobotType;
import battlecode.common.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// {index, adds: {team:{0: [updates], 1: [updates]}}, removals: {team:{0: [updates], 1: [updates]}}}
public class MMapUpdates extends MMessage {
    byte index;
    Map<Team, MMapUpdate[]> adds;
    Map<Team, MMapUpdate[]> removals;

    public MMapUpdates(byte index, Map<Team, MMapUpdate[]> adds, Map<Team, MMapUpdate[]> removals) {
        assert adds != null;
        assert removals != null;
        this.index = index;
        this.adds = adds;
        this.removals = removals;
    }

    static MMapUpdates read(byte[] bytes, int from) {
        // 0 byte is header
        from ++;
        byte index = bytes[from++];
        byte addsSize = bytes[from++];
        Map<Team, MMapUpdate[]> adds = new HashMap<>();
        for (int i = 0; i < addsSize; i++) {
            Team key = Team.values()[bytes[from++]];
            int valuesCount = bytes[from++];
            MMapUpdate[] values = new MMapUpdate[valuesCount];
            for (int j = 0; j < valuesCount; j++) {
                values[j] = MMapUpdate.read(bytes, from);
                from += MMapUpdate.size;
            }
            adds.put(key, values);
        }
        byte removalsSize = bytes[from++];
        Map<Team, MMapUpdate[]> removals = new HashMap<>();
        for (int i = 0; i < removalsSize; i++) {
            Team key = Team.values()[bytes[from++]];
            int valuesCount = bytes[from++];
            MMapUpdate[] values = new MMapUpdate[valuesCount];
            for (int j = 0; j < valuesCount; j++) {
                values[j] = MMapUpdate.read(bytes, from);
                from += MMapUpdate.size;
            }
            removals.put(key, values);
        }

        return new MMapUpdates(index, adds, removals);
    }

    static byte header = 1;

    static boolean write(MMapUpdates l, byte[] bytes, int from) {
        bytes[from++] = header;
        bytes[from++] = l.index;
        bytes[from++] = (byte) l.adds.size();
        for (Map.Entry<Team, MMapUpdate[]> entry : l.adds.entrySet()) {
            bytes[from++] = (byte) entry.getKey().ordinal();
            bytes[from++] = (byte) entry.getValue().length;
            for (MMapUpdate update : entry.getValue()) {
                MMapUpdate.write(update, bytes, from);
                from += MMapUpdate.size;
            }
        }
        bytes[from++] = (byte) l.removals.size();
        for (Map.Entry<Team, MMapUpdate[]> entry : l.removals.entrySet()) {
            bytes[from++] = (byte) entry.getKey().ordinal();
            bytes[from++] = (byte) entry.getValue().length;
            for (MMapUpdate update : entry.getValue()) {
                MMapUpdate.write(update, bytes, from);
                from += MMapUpdate.size;
            }
        }
        return from > 28;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MMapUpdates that = (MMapUpdates) o;
        return index == that.index &&
                adds.equals(that.adds) &&
                removals.equals(that.removals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, adds, removals);
    }
}
