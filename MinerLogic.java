package mybot;

import battlecode.common.*;

public strictfp class MinerLogic {
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
            RobotInfo[] robots = RobotPlayer.rc.senseNearbyRobots();
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.HQ && robot.team == RobotPlayer.rc.getTeam()) {
                    hqLoc = robot.location;
                }
            }
        }

        RobotPlayer.tryBlockchain();
        for (Direction dir : DirectionsUtil.directions)
            if (RobotPlayer.tryRefine(dir))
                System.out.println("I refined soup! " + RobotPlayer.rc.getTeamSoup());
        for (Direction dir : DirectionsUtil.directions)
            if (RobotPlayer.tryMine(dir))
                System.out.println("I mined soup! " + RobotPlayer.rc.getSoupCarrying());
        if (RobotPlayer.rc.getSoupCarrying() == RobotType.MINER.soupLimit) {
            // time to go back to the HQ
            Direction dirToHQ = RobotPlayer.rc.getLocation().directionTo(hqLoc);
            if(RobotPlayer.tryMove(dirToHQ))
                System.out.println("moved towards HQ");
        } else if (RobotPlayer.tryMove(DirectionsUtil.randomDirection())) {
            // otherwise, move randomly as usual
            System.out.println("I moved randomly!");
        }
    }
}
