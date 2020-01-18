package mybot;

import battlecode.common.*;

import static mybot.UDirections.randomDirection;

public strictfp class LLandscaper {
    private static int lowestElevation = Integer.MAX_VALUE;
    private static MapLocation bestPlaceToBuildWall = null;

    static boolean findGoodPlaceAndDig() throws GameActionException {
        int minElevation = Integer.MAX_VALUE;
        Direction bestDir = null;
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            int elevation = GS.c.senseElevation(point);
            RobotInfo ri = GS.c.senseRobotAtLocation(point);
            if (point.x % 2 == 0 &&
                    point.y % 2 == 0 &&
                    (ri == null || ri.getTeam() != GS.c.getTeam()) &&
                    elevation < minElevation && GS.c.canDigDirt(dir)) {
                minElevation = elevation;
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
    static void run() throws GameActionException {
        if (SState.hqLoc != null) {
            if (GS.c.isReady()) {
                // todo don't dig intil the water
                int distanceToHQ = GS.c.getLocation().distanceSquaredTo(SState.hqLoc);
                if (GS.c.getDirtCarrying() < RobotType.LANDSCAPER.dirtLimit && !place) {
                    findGoodPlaceAndDig();
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

                for (Direction dir : UDirections.withoutCenter) {
                    MapLocation tileToCheck = SState.hqLoc.add(dir);
                    if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) < lowestElevation) {
                        lowestElevation = GS.c.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                    }
                }

                if (SState.hqLoc != null) {
                    if (GS.c.isReady()) {
                        Direction toHQ = GS.c.getLocation().directionTo(SState.hqLoc);
                        if (bestPlaceToBuildWall == null) {
                            if (!GS.goTo(toHQ)) {
                            }
                        } else if (GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit || place) {
                            // todo log all this shit
                            if (!GS.goTo(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                                if (!GS.goTo(toHQ)) {
                                }
                            }
                        } else {
                            // todo find best params
                            if (Math.random() < 0.5 && distanceToHQ < 4) {
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
