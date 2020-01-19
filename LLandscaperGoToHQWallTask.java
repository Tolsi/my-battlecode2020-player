package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import java.util.HashSet;
import java.util.Set;

public class LLandscaperGoToHQWallTask extends LPathFindingTask {
    private Set<MapLocation> shouldBeLandcaperHere;

    public LLandscaperGoToHQWallTask() {
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
        if (path != null) {
            if (!makeNextStepByPath()) {
                step();
            }
        } else {
            UPatrol.updateDepthOnTheMap();

            int minDistance = Integer.MAX_VALUE;
            MapLocation closestEmptyPlace = null;

            for (MapLocation landscaperPlace : shouldBeLandcaperHere) {
                int distance = GS.c.getLocation().distanceSquaredTo(landscaperPlace);
                if (GS.c.canSenseLocation(landscaperPlace) && !GS.c.isLocationOccupied(landscaperPlace) && distance < minDistance) {
                    closestEmptyPlace = landscaperPlace;
                    minDistance = distance;
                }
            }
            if (closestEmptyPlace != null) {
                if (GS.c.getLocation().distanceSquaredTo(closestEmptyPlace) < 16) {
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
                    } else {
                        if (GS.goTo(GS.c.getLocation().directionTo(closestEmptyPlace))) {

                        }
                    }
                } else {
                    if (GS.goTo(GS.c.getLocation().directionTo(closestEmptyPlace))) {

                    }
                }
            }
            if (path != null) {
                if (!makeNextStepByPath()) {
                    step();
                }
            } else if (GS.c.getLocation().distanceSquaredTo(SState.hqLoc) >= 9) {
                for (Direction dir : UDirections.allDirectionsTo(GS.c.getLocation(), SState.hqLoc)) {
                    if (GS.goTo(dir)) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    boolean isFinished() {
        return GS.c.getLocation().distanceSquaredTo(SState.hqLoc) < 4;
    }
}
