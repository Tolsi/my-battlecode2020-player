package mybot;

import battlecode.common.*;

import java.util.LinkedList;
import java.util.List;

import static mybot.UBlockchain.saveSoupAsByte;

public strictfp class LMiner {
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static boolean findPlaceAndBuild(RobotType type) throws GameActionException {
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            if (SMap.mySoupMap[point.x][point.y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
                if (GS.tryBuild(type, dir)) {
                    System.out.println("created a design school");
                    if (UBlockchain.sendWhatIBuild(point, type)) {
                        System.out.println("write it on blockchain");
                    } else {
                        System.out.println("can't write it on blockchain");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static void run() throws GameActionException {
        boolean canStay = false;

        List<MSoupLocation> newSoupLocations = new LinkedList<>();
        for (Direction dir : UDirections.withoutCenter) {
            if (GS.tryRefine(dir)) {
                System.out.println("I refined soup! " + GS.c.getTeamSoup());
                break;
            }
            if (GS.c.getSoupCarrying() < RobotType.MINER.soupLimit && GS.tryMine(dir)) {
                System.out.println("I mined soup! " + GS.c.getSoupCarrying());
                canStay = true;
                break;
            }
        }

        //region Looking for new soup
        MapLocation l = GS.c.getLocation();
        int radius = (int) Math.sqrt(GS.c.getCurrentSensorRadiusSquared());
        for (int x = l.x - radius; x < l.x + radius; x++) {
            if (x >= 0 && x < GS.c.getMapWidth()) {
                for (int y = l.y - radius; y < l.y + radius; y++) {
                    if (y >= 0 && y < GS.c.getMapHeight()) {
                        MapLocation point = new MapLocation(x, y);
                        if (GS.c.canSenseLocation(point)) {
                            int soupValue = GS.c.senseSoup(point);
                            byte soupBytes = saveSoupAsByte(soupValue);
                            if (SMap.mySoupMap[point.x][point.y] != soupBytes) {
                                SMap.markSoup(point, soupBytes);
                            }
                            if (((soupBytes > UBlockchain.UNSIGNED_BYTE_MIN_VALUE && SMap.blockchainSoupMap[x][y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) ||
                                    (soupBytes == UBlockchain.UNSIGNED_BYTE_MIN_VALUE && soupBytes != SMap.blockchainSoupMap[x][y])) &&
                                    newSoupLocations.size() <= 8) {
                                // todo if it's not on the "state" map say to blockchain that there're a soup
                                newSoupLocations.add(new MSoupLocation(point, soupBytes));
                            }
                        }
                    }
                }
//                System.out.printf("%d: I'm a %s[%d] - soup scan - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());
            }
        }
        //endregion

        //region Write new found soup to blockchain
        //        System.out.printf("%d: I'm a %s[%d] - after soup scan - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());
        if (newSoupLocations.size() > 0) {
            // take only first 8 messages to not get buffer overflow
            newSoupLocations = newSoupLocations.subList(0, Math.min(8, newSoupLocations.size()));
            int[] txData = UBlockchain.messageToTXData(new MSoupLocations(newSoupLocations));
            System.out.printf("Send %d new soup locations to the blockchain\n", newSoupLocations.size());
            int fee = UBlockchain.bestFee();
            if (fee > 0) {
                GS.c.submitTransaction(txData, fee);
            }
        }
        System.out.printf("%d: I'm a %s[%d] - after blockchain send - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());
        //endregion

        //region Make action
        if (GS.c.getTeamSoup() >= RobotType.DESIGN_SCHOOL.cost &&
                SMap.filterBuildingTypes(RobotType.DESIGN_SCHOOL).size() == 0 &&
                Math.random() < 0.1) {
//        GS.nearbyRobots = GS.c.senseNearbyRobots();
            findPlaceAndBuild(RobotType.DESIGN_SCHOOL);
        }

        if (!canStay) {
            MapLocation returnTo = SMap.closestLocations(GS.c.getLocation(), SMap.filterBuildingTypes(RobotType.HQ, RobotType.REFINERY));
            if (GS.c.getSoupCarrying() == RobotType.MINER.soupLimit && returnTo != null) {
                // time to go back to the HQ or refinery
                if (GS.goTo(GS.c.getLocation().directionTo(SMap.hqLoc))) {
                    GS.c.setIndicatorLine(GS.c.getLocation(), SMap.hqLoc, 255, 255, 0);
                    System.out.println("moved towards HQ");
                }
            } else {
                MapLocation closestSoupLocation = SMap.closestLocations(GS.c.getLocation(), SMap.soupLocations);
                if (closestSoupLocation != null) {
                    if (GS.goTo(GS.c.getLocation().directionTo(closestSoupLocation))) {
                        GS.c.setIndicatorLine(GS.c.getLocation(), closestSoupLocation, 255, 255, 0);
                        System.out.printf("moved to closest soup: %s\n", closestSoupLocation);
                    } else {
                        if (GS.tryMove(UDirections.randomDirection())) {
                            // otherwise, move randomly as usual
                            System.out.println("I moved randomly!");
                        }
                    }
                } else if (GS.tryMove(UDirections.randomDirection())) { // todo research location here
                    // otherwise, move randomly as usual
                    System.out.println("I moved randomly!");
                }
            }
        }
        //endregion
    }
}
