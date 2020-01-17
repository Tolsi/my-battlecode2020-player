package mybot;

import battlecode.common.GameActionException;
import battlecode.common.RobotInfo;

public strictfp class LNetGun {
    static void run() throws GameActionException {
        GS.nearbyRobots = GS.c.senseNearbyRobots();
        for (RobotInfo ri: GS.nearbyRobots) {
            if (ri.getTeam() != GS.c.getTeam() && GS.c.canShootUnit(ri.getID())) {
                GS.c.shootUnit(ri.getID());
                GS.c.setIndicatorLine(GS.c.getLocation(), ri.getLocation(), 255, 0, 0);
                System.out.printf("Shoot to %s\n", ri);
            }
        }
    }
}
