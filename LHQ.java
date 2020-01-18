package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public strictfp class LHQ {
    private static int minersCount = 0;
    private static boolean locked;
    static void run() throws GameActionException {
        if (SMap.hqLoc == null) {
            SMap.hqLoc = GS.c.getLocation();
        }

        if (UBlockchain.sendWhatICreated()) {
            System.out.println("Write to blockchain that I was created");
        }

        int normalLevelMin = Integer.MIN_VALUE;
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation tileToCheck = SMap.hqLoc.add(dir).add(dir);
            if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) > normalLevelMin) {
                normalLevelMin = GS.c.senseElevation(tileToCheck);
            }
        }

        int minAroundLevel = Integer.MAX_VALUE;
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation tileToCheck = SMap.hqLoc.add(dir);
            if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) < minAroundLevel) {
                minAroundLevel = GS.c.senseElevation(tileToCheck);
            }
        }

        if (Math.abs(normalLevelMin - minAroundLevel) > 3) {
            locked = true;
            int[] txData = UBlockchain.messageToTXData(new MLocationLocked(GS.c.getLocation()));
            int fee = UBlockchain.bestFee();
            if (fee > 0) {
                GS.c.submitTransaction(txData, fee);
            }
        }

        UDebug.drawBuildingCarryingSoup();

        // todo if there're a lot of soup, more than team target to build
        // todo optimize this parameters?!
        if (!locked && (minersCount < 6 || SMap.buildingsLocations.size() > 2 && GS.c.getTeamSoup() > 200)) {
            for (Direction dir : UDirections.all) {
                if (GS.tryBuild(RobotType.MINER, dir)) {
                    minersCount += 1;
                    System.out.println("made a miner");
                }
            }
        }
    }
}
