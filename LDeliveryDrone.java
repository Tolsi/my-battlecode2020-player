package mybot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public strictfp class LDeliveryDrone {
    static void run() throws GameActionException {
        Team enemy = ARobotPlayer.rc.getTeam().opponent();
        if (!ARobotPlayer.rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = ARobotPlayer.rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                ARobotPlayer.rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            ARobotPlayer.tryMove(UDirections.randomDirection());
        }
    }
}
