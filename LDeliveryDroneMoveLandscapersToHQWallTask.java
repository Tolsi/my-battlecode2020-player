package mybot;

import battlecode.common.*;
import sun.misc.GC;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public strictfp class LDeliveryDroneMoveLandscapersToHQWallTask extends LPathFindingTask {
    private Set<MapLocation> shouldBeLandcaperHere;

    public LDeliveryDroneMoveLandscapersToHQWallTask() {
//        shouldBeLandcaperHere = new HashSet<>(Arrays.asList(
//                new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y + 1),
//                new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y),
//                new MapLocation(SState.hqLoc.x + 1, SState.hqLoc.y - 1),
//                new MapLocation(SState.hqLoc.x, SState.hqLoc.y + 1),
//                new MapLocation(SState.hqLoc.x, SState.hqLoc.y - 1),
//                new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y),
//                new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y - 1),
//                new MapLocation(SState.hqLoc.x - 1, SState.hqLoc.y + 1)
//        ));
        shouldBeLandcaperHere = new HashSet<>();
        for (Direction dir : UDirections.withoutCenter) {
            shouldBeLandcaperHere.add(SState.hqLoc.add(dir));
        }
    }

    @Override
    void step() throws GameActionException {
        if (path != null && GS.c.getLocation().distanceSquaredTo(lastPathPoint()) > 2) {
            if (!makeNextStepByPath()) {
                step();
            }
        } else {
            List<MapLocation> emptyPlaces = new LinkedList<>();
            for (MapLocation landscaperPlace : shouldBeLandcaperHere) {
                if (GS.c.canSenseLocation(landscaperPlace) && !GS.c.isLocationOccupied(landscaperPlace)) {
                    emptyPlaces.add(landscaperPlace);
                }
            }
            if (GS.c.isCurrentlyHoldingUnit()) {
                for (Direction dir : UDirections.withoutCenter) {
                    if (GS.c.canDropUnit(dir)) {
                        if (emptyPlaces.contains(GS.c.getLocation().add(dir))) {
                            GS.c.dropUnit(dir);
                        }
                    }
                }
                if (GS.c.isReady()) {
                    int minDistance = Integer.MAX_VALUE;
                    MapLocation closestEmptyPlace = null;
                    for (MapLocation landscaperPlace : emptyPlaces) {
                        int distance = GS.c.getLocation().distanceSquaredTo(landscaperPlace);
                        if (GS.c.canSenseLocation(landscaperPlace) && distance < minDistance) {
                            closestEmptyPlace = landscaperPlace;
                            minDistance = distance;
                        }
                    }
                    if (closestEmptyPlace != null) {
                        if (GS.c.getLocation().distanceSquaredTo(closestEmptyPlace) < 25) {
                            AAPFGridGraph gg = new AAPFGridGraph(SState.myDepthMap);
                            int[][] path = AAPFUtility.generatePath(gg, GS.c.getLocation().x, GS.c.getLocation().y, closestEmptyPlace.x, closestEmptyPlace.y, 10);

                            // debug draw
                            if (path.length > 1) {
                                for (int i = 0; i < path.length; i++) {
                                    GS.c.setIndicatorDot(new MapLocation(path[i][0], path[i][1]), 255, 255, 255);
                                }
                            } else {
                                GS.c.setIndicatorDot(new MapLocation(GS.c.getLocation().x, GS.c.getLocation().y), 255, 0, 0);
                            }

                            if (path.length > 1) {
                                for (int i = 0; i < path.length; i++) {
                                    GS.c.setIndicatorDot(new MapLocation(path[i][0], path[i][1]), 255, 255, 255);
                                }
                                this.path = path;
                                this.currentPathStep = 0;
                                step();
                            } else {
                                if (GS.goTo(GS.c.getLocation().directionTo(closestEmptyPlace))) {

                                }
                            }
                        } else {
                            if (GS.goTo(GS.c.getLocation().directionTo(closestEmptyPlace))) {

                            }
                        }
                    } else {
                        if (GS.goTo(GS.c.getLocation().directionTo(SState.hqLoc))) {

                        }
                    }
                }
            } else {
                if (shouldBeLandcaperHere.contains(GS.c.getLocation())) {
                    GS.goOut(GS.c.getLocation().directionTo(SState.hqLoc));
                }
                GS.nearbyRobots = GS.c.senseNearbyRobots();
                for (RobotInfo ri : GS.nearbyRobots) {
                    if (ri.getType() == RobotType.LANDSCAPER && ri.getTeam() == GS.c.getTeam()) {
                        if (!shouldBeLandcaperHere.contains(ri.getLocation())) {
                            if (GS.c.canPickUpUnit(ri.getID())) {
                                GS.c.pickUpUnit(ri.getID());
                            } else if (GS.c.getLocation().distanceSquaredTo(ri.getLocation()) >= 4) {
                                GS.goTo(GS.c.getLocation().directionTo(ri.getLocation()));
                            }
                        }
                    }
                }
                if (emptyPlaces.isEmpty() && (!GS.c.canSenseLocation(SState.hqLoc) || GS.c.getLocation().distanceSquaredTo(SState.hqLoc) >= 9)) {
                    for (Direction dir : UDirections.allDirectionsTo(GS.c.getLocation(), SState.hqLoc)) {
                        if (GS.goTo(dir)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    boolean isFinished() throws GameActionException {
        List<MapLocation> emptyPlaces = new LinkedList<>();
        for (MapLocation landscaperPlace : shouldBeLandcaperHere) {
            if (GS.c.canSenseLocation(landscaperPlace) && GS.c.senseRobotAtLocation(landscaperPlace) != null && GS.c.senseRobotAtLocation(landscaperPlace).type == RobotType.LANDSCAPER) {
                emptyPlaces.add(landscaperPlace);
            } else return false;
        }
        return emptyPlaces.isEmpty();
    }
}
