package lectureplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class FulfillmentCenterLogic {
    static void run() throws GameActionException {
        for (Direction dir : DirectionsUtil.directions)
            RobotPlayer.tryBuild(RobotType.DELIVERY_DRONE, dir);
    }
}
