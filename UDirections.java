package mybot;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

import java.util.LinkedList;
import java.util.List;

public class UDirections {
    static Direction[] all = Direction.values();
    static Direction[] withoutCenter = {Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST};

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return all[(int) (Math.random() * all.length)];
    }

    static Direction randomDirectionTo(Direction dir) {
        Direction[] directions = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight()};
        return directions[(int) (Math.random() * directions.length)];
    }

    static List<Direction> allDirectionsTo(MapLocation current, MapLocation to) {
        if (to == null) {
            return null;
        } else {
            List<Direction> directions = new LinkedList<>();
            int dx = to.x - current.x;
            int dy = to.y - current.y;

            if (dy > 0.0D) {
                if (dx > 0.0D) {
                    directions.add(Direction.NORTHEAST);
                } else if (dx < 0.0D) {
                    directions.add(Direction.NORTHWEST);
                }
            } else {
                if (dx > 0.0D) {
                    directions.add(Direction.SOUTHEAST);
                } else if (dx < 0.0D) {
                    directions.add(Direction.SOUTHWEST);
                }
            }

            if (Math.abs(dx) >= Math.abs(dy)) {
                if (dx > 0.0D) {
                    directions.add(Direction.EAST);
                } else if (dx < 0.0D) {
                    directions.add(Direction.WEST);
                }
            }
            if (Math.abs(dy) >= Math.abs(dx)) {
                if (dy > 0.0D) {
                    directions.add(Direction.NORTH);
                } else {
                    directions.add(Direction.SOUTH);
                }
            }

            if (directions.size() == 1) {
                switch (directions.get(0)) {
                    case EAST:
                        directions.add(Direction.NORTHEAST);
                        directions.add(Direction.SOUTHEAST);
                        break;
                    case WEST:
                        directions.add(Direction.NORTHWEST);
                        directions.add(Direction.SOUTHWEST);
                        break;
                    case NORTH:
                        directions.add(Direction.NORTHEAST);
                        directions.add(Direction.NORTHWEST);
                        break;
                    case SOUTH:
                        directions.add(Direction.SOUTHWEST);
                        directions.add(Direction.SOUTHEAST);
                        break;
                }
            }

            return directions;
        }
    }


}
