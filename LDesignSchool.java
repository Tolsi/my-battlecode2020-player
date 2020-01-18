package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class LDesignSchool {
    private static int landscapersCount = 0;

    static void run() throws GameActionException {
        if (landscapersCount < 16 && GS.c.getTeamSoup() >= RobotType.LANDSCAPER.cost) {
            for (Direction dir : UDirections.withoutCenter)
                if (GS.tryBuild(RobotType.LANDSCAPER, dir)) {
                    landscapersCount += 1;
                    System.out.println("made a landscaper");
                }
        }
    }
}
