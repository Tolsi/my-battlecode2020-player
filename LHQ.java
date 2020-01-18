package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public strictfp class LHQ {
    private static int minersCount = 0;
    private static boolean locked;
    static void run() throws GameActionException {
        if (SState.hqLoc == null) {
            SState.hqLoc = GS.c.getLocation();
        }

        if (UBlockchain.sendWhatICreated()) {
            System.out.println("Write to blockchain that I was created");
        }

        UDebug.drawBuildingCarryingSoup();
        if (!locked) {
            boolean allAreLocked = true;
            for (Direction dir : UDirections.withoutCenter) {
                MapLocation tileToCheck = SState.hqLoc.add(dir);
                if (allAreLocked && GS.c.canSenseLocation(tileToCheck) && (
                        (GS.c.senseRobotAtLocation(tileToCheck) == null || GS.c.senseRobotAtLocation(tileToCheck).type == RobotType.MINER)
                                && !GS.c.senseFlooding(tileToCheck))) {
                    allAreLocked = false;
                }
            }

            if (!allAreLocked) {
                int distance2LevelMax = Integer.MIN_VALUE;
                for (Direction dir : UDirections.withoutCenter) {
                    MapLocation tileToCheck = SState.hqLoc.add(dir).add(dir);
                    if (GS.c.canSenseLocation(tileToCheck) && GS.c.senseElevation(tileToCheck) > distance2LevelMax) {
                        distance2LevelMax = GS.c.senseElevation(tileToCheck);
                    }
                }

                allAreLocked = true;
                for (Direction dir : UDirections.withoutCenter) {
                    MapLocation tileToCheck = SState.hqLoc.add(dir);
                    if (allAreLocked && GS.c.canSenseLocation(tileToCheck) && Math.abs(GS.c.senseElevation(tileToCheck) - distance2LevelMax) <= 3) {
                        allAreLocked = false;
                    }
                }
            }

            if (allAreLocked) {
                locked = true;
                int[] txData = UBlockchain.messageToTXData(new MLocationLocked(GS.c.getLocation()));
                int fee = UBlockchain.bestFee();
                if (fee > 0) {
                    GS.c.submitTransaction(txData, fee);
                }
            }

            // todo if there're a lot of soup, more than team target to build
            // todo optimize this parameters?!
            if (minersCount < 6 || SState.buildingsLocations.size() > 2 && GS.c.getTeamSoup() > 200) {
                for (Direction dir : UDirections.all) {
                    if (GS.tryBuild(RobotType.MINER, dir)) {
                        minersCount += 1;
                        System.out.println("made a miner");
                    }
                }
            }
        }
    }
}
