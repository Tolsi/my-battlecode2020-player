package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class LHQ {
    static void run() throws GameActionException {
        for (Direction dir : UDirections.directions)
            MM.tryBuild(RobotType.MINER, dir);
    }
}
