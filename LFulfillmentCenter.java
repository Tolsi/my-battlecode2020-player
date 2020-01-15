package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class LFulfillmentCenter {
    static void run() throws GameActionException {
        for (Direction dir : UDirections.all)
            GS.tryBuild(RobotType.DELIVERY_DRONE, dir);
    }
}
