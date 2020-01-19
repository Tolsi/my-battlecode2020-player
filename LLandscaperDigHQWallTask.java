package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

// todo
public class LLandscaperDigHQWallTask extends LTask {

    private static int lowestElevation = Integer.MAX_VALUE;
    private static MapLocation bestPlaceToBuildWall = null;

    static boolean findGoodPlaceAndDigAroundHQ() throws GameActionException {
        Direction bestDir = null;
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            if (point.distanceSquaredTo(SState.hqLoc) == 4 && GS.c.canDigDirt(dir)) {
                bestDir = dir;
            }
        }
        if (bestDir != null) {
            GS.c.digDirt(bestDir);
            return true;
        }
        return false;
    }

    static boolean place = false;

    @Override
    void step() throws GameActionException {
        if (SState.hqLoc != null && GS.c.isReady()) {
            for (Direction dir : UDirections.withoutCenter) {
                MapLocation tileToCheck = SState.hqLoc.add(dir);
                int elevation;
                if (GS.c.canSenseLocation(tileToCheck) && (elevation = GS.c.senseElevation(tileToCheck)) < lowestElevation) {
                    lowestElevation = elevation;
                    bestPlaceToBuildWall = tileToCheck;
                }
            }
            if (GS.c.getLocation().distanceSquaredTo(bestPlaceToBuildWall) >= 2 &&
                    GS.goTo(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                System.out.println("Going to better point to build");
            } else if (GS.c.getDirtCarrying() < RobotType.LANDSCAPER.dirtLimit && !place) {
                if (findGoodPlaceAndDigAroundHQ()) {
                    System.out.println("Digging...");
                }
            } else if (bestPlaceToBuildWall != null && (place || GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit)) {
                if (GS.c.getLocation().distanceSquaredTo(bestPlaceToBuildWall) < 4 && GS.c.canDepositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                    System.out.printf("building a wall to %s\n", bestPlaceToBuildWall);
                    GS.c.depositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall));
                } else if (GS.c.canDepositDirt(Direction.CENTER)) {
                    System.out.printf("building a wall to %s\n", GS.c.getLocation());
                    GS.c.depositDirt(Direction.CENTER);
                }
                place = GS.c.getDirtCarrying() > 0;
                lowestElevation = Integer.MAX_VALUE;
                bestPlaceToBuildWall = null;
            }
        }
    }

    @Override
    boolean isFinished() {
        return false;
    }
}
