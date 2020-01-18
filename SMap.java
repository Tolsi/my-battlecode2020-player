package mybot;

import battlecode.common.*;

import java.util.*;

public class SMap {
    /**
     * soup is divided to 10 here
     */
    static byte[][] mySoupMap;
    static byte[][] blockchainSoupMap;

    static Map<MapLocation, Byte> soupLocations;
    static Map<MapLocation, RobotType> buildingsLocations;
    //    private static RobotInfo[][] robots;
    static MapLocation hqLoc;
    static Set<MapLocation> noWayTo;

    static void init(RobotController rc) throws GameActionException {
        noWayTo = new HashSet<>();
        soupLocations = new HashMap<>();
        buildingsLocations = new HashMap<>();
        mySoupMap = new byte[rc.getMapWidth()][];
        for (byte i = 0; i < rc.getMapWidth(); i++) {
            mySoupMap[i] = new byte[rc.getMapHeight()];
            for (byte j = 0; j < rc.getMapHeight(); j++) {
                mySoupMap[i][j] = UBlockchain.UNSIGNED_BYTE_MIN_VALUE;
            }
        }
        blockchainSoupMap = new byte[rc.getMapWidth()][];
        for (byte i = 0; i < rc.getMapWidth(); i++) {
            blockchainSoupMap[i] = new byte[rc.getMapHeight()];
            for (byte j = 0; j < rc.getMapHeight(); j++) {
                blockchainSoupMap[i][j] = UBlockchain.UNSIGNED_BYTE_MIN_VALUE;
            }
        }
        for (int i = 1; i <= rc.getRoundNum() - 1; i++) {
            update(rc, i);
        }
    }

    static void update(RobotController rc, int block) throws GameActionException {
        Transaction[] txs = rc.getBlock(block);
        for (Transaction tx : txs) {
            MMessage message = UBlockchain.messageFromTXData(tx.getMessage());
            if (message != null) {
                if (message instanceof MSoupLocations) {
                    MSoupLocations m = (MSoupLocations) message;
//                    System.out.printf("Got %d new soup locations to the blockchain\n", m.locations.size());
                    for (MSoupLocation lm : m.locations) {
                        markSoup(lm.location, lm.value);
                        blockchainSoupMap[lm.location.x][lm.location.y] = lm.value;
                    }
                } else if (message instanceof MMapUpdates) {
                    MMapUpdates m = (MMapUpdates) message;
                    // todo find my HQ from blockchain message
                    List<MMapUpdate> myTeamUpdates = m.adds.get(GS.c.getTeam());
                    for (MMapUpdate update : myTeamUpdates) {
                        if (update.type == RobotType.HQ) {
                            hqLoc = update.location;
                        }
                        buildingsLocations.put(update.location, update.type);
                    }
                } else if (message instanceof MLocationLocked) {
                    noWayTo.add(((MLocationLocked) message).location);
                }
            }
        }
    }

    static int getSoup(int x, int y) {
        if (mySoupMap[x][y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) {
            return UBlockchain.readSoupFromByte(blockchainSoupMap[x][y]);
        } else {
            return UBlockchain.readSoupFromByte(mySoupMap[x][y]);
        }
    }

    static void markSoup(MapLocation l, byte soup) {
        mySoupMap[l.x][l.y] = soup;
        if (UBlockchain.UNSIGNED_BYTE_MIN_VALUE == soup) {
            soupLocations.remove(l);
        } else {
            soupLocations.put(l, soup);
        }

    }

    static MapLocation closestLocations(MapLocation to, Map<MapLocation, ?> among) {
        if (soupLocations.isEmpty()) {
            return null;
        } else {
            List<MapLocation> locations = new LinkedList<>(among.keySet());
            int minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < locations.size(); i++) {
                MapLocation loc = locations.get(i);
                int distance = to.distanceSquaredTo(loc);
                if (distance < minDistance) {
                    minDistance = distance;
                    minIndex = i;
                }
            }
            if (minIndex == -1) {
                return null;
            } else {
                return locations.get(minIndex);
            }
        }
    }

    static Map<MapLocation, RobotType> filterBuildingTypes(boolean checkNoWay, RobotType... types) {
        Set<RobotType> typesSet = new HashSet<>(Arrays.asList(types));
        Map<MapLocation, RobotType> result = new HashMap<>();
        for (Map.Entry<MapLocation, RobotType> kv : buildingsLocations.entrySet()) {
            if (typesSet.contains(kv.getValue()) && (!checkNoWay || !noWayTo.contains(kv.getKey()))) {
                result.put(kv.getKey(), kv.getValue());
            }
        }
        return result;
    }

    static Map<MapLocation, RobotType> filterBuildingTypes(RobotType... types) {
        return filterBuildingTypes(true, types);
    }

    static boolean nearbyExistsMy(RobotType type) {
        for (RobotInfo rt : GS.nearbyRobots) {
            if (rt.getTeam() == GS.c.getTeam() && rt.getType() == type) {
                return true;
            }
        }
        return false;
    }
}
