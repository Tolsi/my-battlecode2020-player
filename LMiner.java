package mybot;

import battlecode.common.*;

public strictfp class LMiner {
    static MapLocation hqLoc;
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};
    
    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static void run() throws GameActionException {
        if (hqLoc == null) {
            // search surroundings for HQ
            RobotInfo[] robots = GS.c.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == GS.c.getTeam()) {
                    hqLoc = robot.location;
                }
            }
        }

        GS.tryBlockchain();
        for (Direction dir : UDirections.directions)
            if (GS.tryRefine(dir))
                System.out.println("I refined soup! " + GS.c.getTeamSoup());
        for (Direction dir : UDirections.directions)
            if (GS.tryMine(dir))
                System.out.println("I mined soup! " + GS.c.getSoupCarrying());
        if (GS.c.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // time to go back to the HQ
            Direction dirToHQ = GS.c.getLocation().directionTo(hqLoc);
            if(GS.tryMove(dirToHQ))
                System.out.println("moved towards HQ");
        } else if (GS.tryMove(UDirections.randomDirection())) {
            // otherwise, move randomly as usual
            System.out.println("I moved randomly!");
        }
    }
}
