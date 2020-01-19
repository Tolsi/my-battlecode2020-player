package mybot;

import battlecode.common.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public strictfp class LDeliveryDroneRandomAttackUnits extends LTask {
    @Override
    void step() throws GameActionException {
        Team enemy = GS.c.getTeam().opponent();
        if (!GS.c.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = GS.c.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                GS.c.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            GS.tryMove(UDirections.randomDirection());
        }
    }

    @Override
    boolean isFinished() throws GameActionException {
        return false;
    }
}
