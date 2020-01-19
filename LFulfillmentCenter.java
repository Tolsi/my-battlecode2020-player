package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class LFulfillmentCenter {
    private static int dronsCount = 0;
    static void run() throws GameActionException {
        if (UBlockchain.sendWhatICreated()) {
            System.out.println("Write to blockchain that I was created");
        }
        if (dronsCount < 2) {
            for (Direction dir : UDirections.all) {
                if (GS.tryBuild(RobotType.DELIVERY_DRONE, dir)) {
                    dronsCount += 1;
                }
            }
        }
    }
}
