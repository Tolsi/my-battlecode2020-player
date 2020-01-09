package mybot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public strictfp class DeliveryDroneLogic {
    static void run() throws GameActionException {
        Team enemy = RobotPlayer.rc.getTeam().opponent();
        if (!RobotPlayer.rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = RobotPlayer.rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                RobotPlayer.rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            RobotPlayer.tryMove(DirectionsUtil.randomDirection());
        }
    }
}
