package mybot;

import battlecode.common.GameActionException;

public strictfp class LRefinery {
    static void run() throws GameActionException {
        UDebug.drawBuildingCarryingSoup();
        System.out.println("Pollution: " + GS.c.sensePollution(GS.c.getLocation()));
    }
}
