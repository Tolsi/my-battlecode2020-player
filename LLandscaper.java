package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static mybot.UDirections.randomDirection;

public strictfp class LLandscaper {
    private static int lowestElevation = Integer.MAX_VALUE;
    private static MapLocation bestPlaceToBuildWall = null;

    private static Set<MapLocation> shouldBeLandcaperHere;
    private static int[][] path;

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

    static void run() throws GameActionException {
        if (SState.hqLoc != null) {
            if (shouldBeLandcaperHere == null) {
                shouldBeLandcaperHere = new HashSet<>(Arrays.asList(
                        new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y + 1),
                        new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y),
                        new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y - 1),
                        new MapLocation(SState.hqLoc.x, SState.hqLoc.y + 1),
                        new MapLocation(SState.hqLoc.x, SState.hqLoc.y - 1),
                        new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y),
                        new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y - 1),
                        new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y + 1)
                ));
            }
            if (GS.c.isReady()) {
                // todo don't dig intil the water
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

                for (Direction dir : UDirections.withoutCenter) {
                    MapLocation tileToCheck = SState.hqLoc.add(dir);
                    if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) < lowestElevation) {
                        lowestElevation = GS.c.senseElevation(tileToCheck);
                        bestPlaceToBuildWall = tileToCheck;
                    }
                }

                UPatrol.updateDepthOnTheMap();
                AAPFGridGraph gg = new AAPFGridGraph(SState.myDepthMap);
                int[][] path = AAPFUtility.generatePath(gg, GS.c.getLocation().x, GS.c.getLocation().y, SState.hqLoc.add(SState.hqLoc.directionTo(GS.c.getLocation())).x, SState.hqLoc.add(SState.hqLoc.directionTo(GS.c.getLocation())).y, 10);
                if (path.length > 1) {
                    for (int i = 0; i < path.length; i++) {
                        GS.c.setIndicatorDot(new MapLocation(path[i][0], path[i][1]), 255, 255, 255);
                    }
                } else {
                    GS.c.setIndicatorDot(new MapLocation(GS.c.getLocation().x, GS.c.getLocation().y), 255, 0, 0);
                }

                // todo становиться на места, не копать, пока все не на местах. встал на место - не двигайся - после поиска пути
                if (GS.c.isReady() && SState.hqLoc != null) {
                    Direction toHQ = GS.c.getLocation().directionTo(SState.hqLoc);
                    if (bestPlaceToBuildWall == null) {
                        if (!GS.goTo(toHQ)) {
                        }
                    } else if (GS.c.getDirtCarrying() == RobotType.LANDSCAPER.dirtLimit || place) {
                        // todo log all this shit
                        if (GS.goTo(GS.c.getLocation().directionTo(bestPlaceToBuildWall))) {
                            System.out.println("Go to best place to build wall");
                        }
                    } else {
                        // todo find best params
                        int distanceToHQ = GS.c.getLocation().distanceSquaredTo(SState.hqLoc);
                        if (Math.random() < 0.5 && distanceToHQ < 4) {
                            GS.tryMove(randomDirection());
                        } else {
                            if (!GS.goTo(toHQ)) {
                                GS.tryMove(randomDirection());
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
