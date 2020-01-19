package mybot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

/**
 * Represents the Grid of blocked/unblocked tiles.
 */
public class AAPFGridGraph {

    private int[][] tiles;
    public final int sizeX;
    public final int sizeY;
    public final int sizeXplusOne;

    public AAPFGridGraph(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeXplusOne = sizeX + 1;

        tiles = new int[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++) {
            tiles[i] = new int[sizeY];
        }
    }

    public AAPFGridGraph(int[][] map) {
        this.sizeX = map.length;
        this.sizeY = map[0].length;
        this.sizeXplusOne = sizeX + 1;

        tiles = map;
    }

    public void setDepth(int x, int y, int value) {
        tiles[x][y] = value;
    }

    public void trySetDepth(int x, int y, int value) {
        if (isValidBlock(x, y))
            tiles[x][y] = value;
    }

    public boolean isBlocked(int fromx, int fromy, int x, int y) {
        if (x >= sizeX || y >= sizeY) return true;
        if (x < 0 || y < 0) return true;
        // todo invisible is locked or not?!
        MapLocation from = new MapLocation(fromx, fromy);
        MapLocation to = new MapLocation(x, y);
        try {
            // учесть уровень воды через N ходов?
            return Math.abs(tiles[x][y] - tiles[fromx][fromy]) > 3 ||
                    (GS.c.canSenseLocation(to) && (GS.c.senseFlooding(to) || GS.c.senseRobotAtLocation(to) != null && GS.c.senseRobotAtLocation(to).getID() != GS.c.getID())) ||
                    (!GS.c.canSenseLocation(to) && tiles[x][y] != Integer.MIN_VALUE && tiles[x][y] < GS.waterLevel) ||
                    (GS.c.canSenseLocation(from) && (GS.c.senseFlooding(from) || GS.c.senseRobotAtLocation(from) != null && GS.c.senseRobotAtLocation(from).getID() != GS.c.getID())) ||
                    (!GS.c.canSenseLocation(from) && tiles[fromx][fromy] != Integer.MIN_VALUE && tiles[fromx][fromy] < GS.waterLevel);
        } catch (GameActionException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean isValidCoordinate(int x, int y) {
        return (x <= sizeX && y <= sizeY &&
                x >= 0 && y >= 0);
    }

    public boolean isValidBlock(int x, int y) {
        return (x < sizeX && y < sizeY &&
                x >= 0 && y >= 0);
    }

    public int toOneDimIndex(int x, int y) {
        return y * sizeXplusOne + x;
    }

    public int toTwoDimX(int index) {
        return index % sizeXplusOne;
    }

    public int toTwoDimY(int index) {
        return index / sizeXplusOne;
    }

    /**
     * x1,y1,x2,y2 refer to the top left corner of the tile.
     *
     * @param x1 Condition: x1 between 0 and sizeX inclusive.
     * @param y1 Condition: y1 between 0 and sizeY inclusive.
     * @param x2 Condition: x2 between 0 and sizeX inclusive.
     * @param y2 Condition: y2 between 0 and sizeY inclusive.
     * @return distance.
     */
    public int distance(int x1, int y1, int x2, int y2) {
        int xDiff = x2 - x1;
        int yDiff = y2 - y1;

        if (xDiff == 0) {
            return (int) Math.abs(yDiff);
        }
        if (yDiff == 0) {
            return (int) Math.abs(xDiff);
        }
        if (xDiff == yDiff || xDiff == -yDiff) {
            return 2 * Math.abs(xDiff);
        }

        return xDiff * xDiff + yDiff * yDiff;
    }

    /**
     * Same as lineOfSight, but only works with a vertex and its 8 immediate neighbours.
     * Also (x1,y1) != (x2,y2)
     */
    public boolean neighbourLineOfSight(int x1, int y1, int x2, int y2) {
        return !isBlocked(x1, y1, x2, y2);
    }
}
