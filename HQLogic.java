package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class HQLogic {
    static void run() throws GameActionException {
        for (Direction dir : DirectionsUtil.directions)
            RobotPlayer.tryBuild(RobotType.MINER, dir);
    }
}
