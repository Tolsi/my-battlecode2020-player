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
            RobotInfo[] robots = MM.c.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == MM.c.getTeam()) {
                    hqLoc = robot.location;
                }
            }
        }

        MM.tryBlockchain();
        for (Direction dir : UDirections.directions)
            if (MM.tryRefine(dir))
                System.out.println("I refined soup! " + MM.c.getTeamSoup());
        for (Direction dir : UDirections.directions)
            if (MM.tryMine(dir))
                System.out.println("I mined soup! " + MM.c.getSoupCarrying());
        if (MM.c.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // time to go back to the HQ
            Direction dirToHQ = MM.c.getLocation().directionTo(hqLoc);
            if(MM.tryMove(dirToHQ))
                System.out.println("moved towards HQ");
        } else if (MM.tryMove(UDirections.randomDirection())) {
            // otherwise, move randomly as usual
            System.out.println("I moved randomly!");
        }
    }
}
