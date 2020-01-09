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
            RobotInfo[] robots = ARobotPlayer.rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == ARobotPlayer.rc.getTeam()) {
                    hqLoc = robot.location;
                }
            }
        }

        ARobotPlayer.tryBlockchain();
        for (Direction dir : UDirections.directions)
            if (ARobotPlayer.tryRefine(dir))
                System.out.println("I refined soup! " + ARobotPlayer.rc.getTeamSoup());
        for (Direction dir : UDirections.directions)
            if (ARobotPlayer.tryMine(dir))
                System.out.println("I mined soup! " + ARobotPlayer.rc.getSoupCarrying());
        if (ARobotPlayer.rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // time to go back to the HQ
            Direction dirToHQ = ARobotPlayer.rc.getLocation().directionTo(hqLoc);
            if(ARobotPlayer.tryMove(dirToHQ))
                System.out.println("moved towards HQ");
        } else if (ARobotPlayer.tryMove(UDirections.randomDirection())) {
            // otherwise, move randomly as usual
            System.out.println("I moved randomly!");
        }
    }
}
