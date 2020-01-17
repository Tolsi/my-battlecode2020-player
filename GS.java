package mybot;

import battlecode.common.*;

public strictfp class GS {
    static RobotController c;
    static int lifespan;
    static SMap map;
    static RobotInfo[] nearbyRobots = null;

    static boolean tryMove() throws GameActionException {
        for (Direction dir : UDirections.all)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    // checks if the direction is legal and not flooded
    static boolean safeToMove(Direction dir) throws GameActionException {
        return !c.senseFlooding(c.getLocation().add(dir));
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, boolean checkSafe) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (c.isReady() && c.canMove(dir) && (!checkSafe || safeToMove(dir))) {
            c.move(dir);
            return true;
        } else return false;
    }

    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir, true);
    }

    static boolean goTo(Direction dir) throws GameActionException {
        Direction[] toTry = {dir, dir.rotateLeft(), dir.rotateRight(), dir.rotateLeft().rotateLeft(), dir.rotateRight().rotateRight()};
        for (Direction d : toTry) {
            if (tryMove(d))
                return true;
        }
        return false;
    }

    static boolean goOut(Direction dir) throws GameActionException {
        return goTo(dir.opposite());
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir  The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (c.isReady() && c.canBuildRobot(type, dir)) {
            c.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (c.isReady() && c.canMineSoup(dir)) {
            c.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (c.isReady() && c.canDepositSoup(dir)) {
            c.depositSoup(dir, c.getSoupCarrying());
            return true;
        } else return false;
    }

    static void sendToBlockchain(MMessage m) throws GameActionException {
        int[] msg = UBlockchain.messageToTXData(m);
        if (c.canSubmitTransaction(msg, 10))
            c.submitTransaction(msg, 10);
    }

    static boolean tryDig() throws GameActionException {
        int maxElevation = Integer.MIN_VALUE;
        Direction bestDir = null;
        for (Direction dir : UDirections.all) {
            int elevation = GS.c.senseElevation(GS.c.getLocation().add(dir));
            if (elevation > maxElevation && GS.c.canDigDirt(dir)) {
                maxElevation = elevation;
                bestDir = dir;
            }
        }
        if (bestDir != null) {
            GS.c.digDirt(bestDir);
            return true;
        }
        return false;
    }
}
