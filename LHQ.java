package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public strictfp class LHQ {
    private static int minersCount = 0;
    static void run() throws GameActionException {
        if (UBlockchain.sendWhatICreated()) {
            System.out.println("Write to blockchain that I was created");
        }
        // todo if there're a lot of soup, more than team target to build
        // todo optimize this parameters?!
        if (minersCount < 6 || SMap.buildingsLocations.size() > 2 && GS.c.getTeamSoup() > 200) {
            for (Direction dir : UDirections.all) {
                GS.tryBuild(RobotType.MINER, dir);
                minersCount += 1;
            }
        }
    }
}
