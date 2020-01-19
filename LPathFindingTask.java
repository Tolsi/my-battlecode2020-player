package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

public abstract class LPathFindingTask extends LTask {
    protected int[][] path = null;
    protected int currentPathStep = 0;

    protected MapLocation nextPathPoint() {
        if (path != null) {
            int[] nextPoint = path[currentPathStep + 1];
            return new MapLocation(nextPoint[0], nextPoint[1]);
        } else {
            return null;
        }
    }


    protected MapLocation currentPathPoint() {
        if (path != null) {
            int[] nextPoint = path[currentPathStep];
            return new MapLocation(nextPoint[0], nextPoint[1]);
        } else {
            return null;
        }
    }

    protected MapLocation lastPathPoint() {
        if (path != null) {
            int[] lastPoint = path[path.length - 1];
            return new MapLocation(lastPoint[0], lastPoint[1]);
        } else {
            return null;
        }
    }

    protected boolean makeNextStepByPath() throws GameActionException {
        Direction dir = GS.c.getLocation().directionTo(nextPathPoint());
        if (GS.tryMove(dir)) {
            currentPathStep += 1;
            System.out.println("Move by path :>");
            if (currentPathStep + 1 >= path.length) {
                this.path = null;
                this.currentPathStep = 0;
                System.out.println("Looks like I finished it! :>");
            }
            return true;
        } else {
            System.out.println("Can't move by path, reset the path :<");
            this.path = null;
            return false;
        }
    }
}
