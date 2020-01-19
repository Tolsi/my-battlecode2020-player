package mybot;

import battlecode.common.*;

import java.util.List;
import java.util.Set;

public strictfp class LMiner {
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static boolean findPlaceAndBuild(RobotType type) throws GameActionException {
        return findPlaceAndBuild(type, null);
    }

    static boolean findPlaceAndBuild(RobotType type, Set<Integer> equalsRangeToHQ) throws GameActionException {
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            // todo find hight place
            if (SState.mySoupMap[point.x][point.y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE &&
                    (equalsRangeToHQ == null || (SState.hqLoc != null && equalsRangeToHQ.contains(point.distanceSquaredTo(SState.hqLoc))))) {
                if (GS.tryBuild(type, dir)) {
                    SState.buildingsLocations.put(point, type);
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

    static boolean findPlaceAndBuildFarMoreThat(RobotType type, int moreRangeToHQ) throws GameActionException {
        for (Direction dir : UDirections.withoutCenter) {
            MapLocation point = GS.c.getLocation().add(dir);
            if (SState.mySoupMap[point.x][point.y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE && (SState.hqLoc != null && point.distanceSquaredTo(SState.hqLoc) > moreRangeToHQ)) {
                if (GS.tryBuild(type, dir)) {
                    SState.buildingsLocations.put(point, type);
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
                    !SState.nearbyExistsMy(RobotType.NET_GUN)) {
                findPlaceAndBuild(RobotType.NET_GUN);
            }
        }
        //endregion

        // todo if can get it? :D
        MapLocation closestHqOrRefinery = SState.closestLocations(GS.c.getLocation(), SState.filterBuildingTypes(RobotType.HQ, RobotType.REFINERY));
        if (GS.c.getTeamSoup() >= RobotType.REFINERY.cost &&
                (soupAround > 0 && closestHqOrRefinery == null && SState.filterBuildingTypes(RobotType.DESIGN_SCHOOL).size() > 0) ||
                (soupAround > 1000 && closestHqOrRefinery != null && (int) Math.sqrt(GS.c.getLocation().distanceSquaredTo(closestHqOrRefinery)) > Math.min(GS.c.getMapHeight(), GS.c.getMapWidth()) / 3) ||
                (closestHqOrRefinery != null && SState.filterBuildingTypes(RobotType.REFINERY).size() == 0 && !SState.nearbyExistsMy(RobotType.REFINERY) && Math.random() < 0.05)) {
            findPlaceAndBuildFarMoreThat(RobotType.REFINERY, 64);
        }

        if (GS.c.getTeamSoup() >= RobotType.DESIGN_SCHOOL.cost &&
                (SState.filterBuildingTypes(RobotType.REFINERY).size() > 0 ||
                        GS.c.getTeamSoup() >= (RobotType.DESIGN_SCHOOL.cost + RobotType.REFINERY.cost)) &&
                SState.filterBuildingTypes(RobotType.DESIGN_SCHOOL).size() == 0 &&
                Math.random() < 0.1) {
            findPlaceAndBuildFarMoreThat(RobotType.DESIGN_SCHOOL, 8);
        }

        // todo поиск пути по высотам, которые знаешь
        // todo строить на самой высокой точке, на которую знаешь знаешь как пройти
        // todo делиться самой высокой точкой, до которой знаешь, как дойти? строит кто знает самую высокую
        // todo строить частичную матрицу смежности? откуда куда можно, а куда нельзя?
        // todo двигаться напрямую для летатетей, но облетать вышки (отмечать их на блокчейне) с помощью полей потенциалов
        // todo a star при маленьком расстоянии до точки? и лимит в количестве шагов?
        // todo сжатие карты как изображения без потери качества для передачи?
        if (GS.c.getTeamSoup() >= RobotType.FULFILLMENT_CENTER.cost &&
                (SState.filterBuildingTypes(RobotType.REFINERY).size() > 0 &&
                        SState.filterBuildingTypes(RobotType.DESIGN_SCHOOL).size() > 0) &&
                SState.filterBuildingTypes(RobotType.FULFILLMENT_CENTER).size() == 0 &&
                Math.random() < 0.1) {
            findPlaceAndBuildFarMoreThat(RobotType.FULFILLMENT_CENTER, 8);
        }

        //region Make action
        if (!canStay) {
            MapLocation returnTo = SState.closestLocations(GS.c.getLocation(), SState.filterBuildingTypes(RobotType.HQ, RobotType.REFINERY));
            if (returnTo == null) {
                System.out.println("OMFG where to go?!");
            }
            if (GS.c.getSoupCarrying() == RobotType.MINER.soupLimit && returnTo != null) {
                // time to go back to the HQ or refinery
                if (GS.goTo(GS.c.getLocation().directionTo(returnTo))) {
                    GS.c.setIndicatorLine(GS.c.getLocation(), returnTo, 255, 255, 0);
                    System.out.println("moved towards closest REFINERY or HQ");
                }
            } else {
                MapLocation closestSoupLocation = SState.closestLocations(GS.c.getLocation(), SState.soupLocations);
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
                } else if (SState.hqLoc != null && GS.c.getLocation().distanceSquaredTo(SState.hqLoc) < 4) {
                    if (GS.tryMove(UDirections.randomDirection())) { // todo research location here
                        // otherwise, move randomly as usual
                        System.out.println("I moved randomly!");
                    }
                } else {
                    if (GS.goOut(GS.c.getLocation().directionTo(SState.hqLoc))) {
                        System.out.println("I moved out of HQ!");
                    }
                }
            }
        }
        //endregion
    }
}
