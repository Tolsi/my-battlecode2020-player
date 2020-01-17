package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import static mybot.UDirections.randomDirection;

public strictfp class LLandscaper {
    private static int lowestElevation = Integer.MAX_VALUE;
    private static MapLocation bestPlaceToBuildWall = null;

    static void run() throws GameActionException {
        if (SMap.hqLoc != null) {
            if (GS.c.isReady()) {
                // todo don't dig intil the water
                int distanceToHQ = GS.c.getLocation().distanceSquaredTo(SMap.hqLoc);
                if (GS.c.getDirtCarrying() < RobotType.LANDSCAPER.dirtLimit && distanceToHQ >= 16) {
                    GS.tryDig();
                } else if (bestPlaceToBuildWall != null &&
                        GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit &&
                        GS.c.getLocation().distanceSquaredTo(bestPlaceToBuildWall) < 4 &&
                        GS.c.canDepositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                    GS.c.depositDirt(GS.c.getLocation().directionTo(bestPlaceToBuildWall));
                    lowestElevation = Integer.MAX_VALUE;
                    bestPlaceToBuildWall = null;
                    System.out.println("building a wall");
                }

                for (Direction dir : UDirections.withoutCenter) {
                    MapLocation tileToCheck = SMap.hqLoc.add(dir);
                    if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) < lowestElevation) {
                        lowestElevation = GS.c.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                    }
                }

                if (SMap.hqLoc != null) {
                    if (GS.c.isReady()) {
                        Direction toHQ = GS.c.getLocation().directionTo(SMap.hqLoc);
                        if (bestPlaceToBuildWall != null && GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit) {
                            // todo log all this shit
                            if (!GS.goTo(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                                if (!GS.goTo(toHQ)) {
                                }
                            }
                        } else {
                            if (Math.random() < 0.4 && distanceToHQ < 9) {
                                GS.tryMove(randomDirection());
                            } else {
                                if (!GS.goOut(toHQ)) {
                                    GS.tryMove(randomDirection());
                                }
                            }
                        }
                    }
                }
            }
        } else {
            GS.tryMove(randomDirection());
        }
    }
}
