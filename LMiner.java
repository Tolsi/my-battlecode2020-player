package mybot;

import battlecode.common.*;

import java.util.List;

public strictfp class LMiner {
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static boolean findPlaceAndBuild(RobotType type) throws GameActionException {
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            // todo or min?
            if (SMap.mySoupMap[point.x][point.y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
                if (GS.tryBuild(type, dir)) {
                    SMap.buildingsLocations.put(point, type);
                    System.out.printf("created a %s", type);
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

        System.out.printf("%d: I'm a %s[%d] - before mark new soup - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());

        //region Looking for new soup
        UTuple2<List<MSoupLocation>, Integer> soupLocationsResult = UPatrol.markNewSoupLocationsOnTheMapAndToBlockchain();
        List<MSoupLocation> newSoupLocations = soupLocationsResult.f;
        int soupAround = soupLocationsResult.s;
        //endregion

        System.out.printf("%d: I'm a %s[%d] - after mark new soup - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());

        //region Write new found soup to blockchain
        UPatrol.sendNewSoupLocationsToBlockchain(newSoupLocations);
        //endregion

        System.out.printf("%d: I'm a %s[%d] - after blockchain send - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());

        //region build net gun is there're no any
        GS.nearbyRobots = GS.c.senseNearbyRobots();
        for (RobotInfo ri : GS.nearbyRobots) {
            if (ri.getTeam() != GS.c.getTeam() &&
                    ri.getType() == RobotType.DELIVERY_DRONE &&
                    GS.c.getTeamSoup() >= RobotType.NET_GUN.cost &&
                    !SMap.nearbyExistsMy(RobotType.NET_GUN)) {
                findPlaceAndBuild(RobotType.NET_GUN);
            }
        }
        //endregion

        // todo if can get it? :D
        MapLocation closestHqOrRefinery = SMap.closestLocations(GS.c.getLocation(), SMap.filterBuildingTypes(RobotType.HQ, RobotType.REFINERY));
        if (GS.c.getTeamSoup() >= RobotType.REFINERY.cost &&
                (soupAround > 0 && closestHqOrRefinery == null && GS.c.getSoupCarrying() == RobotType.MINER.soupLimit) ||
                (soupAround > 1000 && closestHqOrRefinery != null && GS.c.getLocation().distanceSquaredTo(closestHqOrRefinery) > 200 &&
                !SMap.nearbyExistsMy(RobotType.REFINERY)) ||
                (soupAround > 0 && SMap.filterBuildingTypes(RobotType.REFINERY).size() == 0 && closestHqOrRefinery != null && GS.c.getLocation().distanceSquaredTo(closestHqOrRefinery) > 100)) {
            findPlaceAndBuild(RobotType.REFINERY);
        }

        if (GS.c.getTeamSoup() >= RobotType.DESIGN_SCHOOL.cost &&
                SMap.filterBuildingTypes(RobotType.REFINERY).size() > 0 &&
                SMap.filterBuildingTypes(RobotType.DESIGN_SCHOOL).size() == 0 &&
                Math.random() < 0.1) {
//        GS.nearbyRobots = GS.c.senseNearbyRobots();
            findPlaceAndBuild(RobotType.DESIGN_SCHOOL);
        }

        //region Make action
        if (!canStay) {
            MapLocation returnTo = SMap.closestLocations(GS.c.getLocation(), SMap.filterBuildingTypes(RobotType.HQ, RobotType.REFINERY));
            if (GS.c.getSoupCarrying() == RobotType.MINER.soupLimit && returnTo != null) {
                // time to go back to the HQ or refinery
                if (GS.goTo(GS.c.getLocation().directionTo(returnTo))) {
                    GS.c.setIndicatorLine(GS.c.getLocation(), returnTo, 255, 255, 0);
                    System.out.println("moved towards closest REFINERY or HQ");
                }
            } else {
                MapLocation closestSoupLocation = SMap.closestLocations(GS.c.getLocation(), SMap.soupLocations);
                if (closestSoupLocation != null) {
                    // todo call to landscaper to break the wall
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
