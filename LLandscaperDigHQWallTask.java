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
            for (Direction dir : UDirections.all) {
                MapLocation tileToCheck = GS.c.getLocation().add(dir);
                int distanceToHq = tileToCheck.distanceSquaredTo(SState.hqLoc);
                if (GS.c.canSenseLocation(tileToCheck) && distanceToHq < 4 && distanceToHq > 0 && GS.c.senseElevation(tileToCheck) < lowestElevation) {
                    lowestElevation = GS.c.senseElevation(tileToCheck);
                    bestPlaceToBuildWall = tileToCheck;
                }
            }

            if (GS.c.getDirtCarrying() < RobotType.LANDSCAPER.dirtLimit && !place) {
                findGoodPlaceAndDigAroundHQ();
            } else if (bestPlaceToBuildWall != null &&
                    GS.c.getLocation().distanceSquaredTo(bestPlaceToBuildWall) < 4 &&
                    GS.c.canDepositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall)) &&
                    (place || GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit)) {
                GS.c.depositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall));
                lowestElevation = Integer.MAX_VALUE;
                bestPlaceToBuildWall = null;
                System.out.println("building a wall");
                place = GS.c.getDirtCarrying() > 0;
            }
        }
    }

    @Override
    boolean isFinished() {
        return false;
    }
}
