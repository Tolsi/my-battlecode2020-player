package mybot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

import java.util.LinkedList;
import java.util.List;

import static mybot.UBlockchain.saveSoupAsByte;

public class UPatrol {
    static UTuple2<List<MSoupLocation>, Integer> markNewSoupLocationsOnTheMapAndToBlockchain() throws GameActionException {
        List<MSoupLocation> newSoupLocations = new LinkedList<>();
        int soupAround = 0;
        MapLocation l = GS.c.getLocation();
        int radius = (int) Math.sqrt(GS.c.getCurrentSensorRadiusSquared());
        for (int x = l.x - radius; x < l.x + radius; x++) {
            if (x >= 0 && x < GS.c.getMapWidth()) {
                for (int y = l.y - radius; y < l.y + radius; y++) {
                    if (y >= 0 && y < GS.c.getMapHeight()) {
                        MapLocation point = new MapLocation(x, y);
                        if (GS.c.canSenseLocation(point)) {
                            int soupValue = GS.c.senseSoup(point);
                            soupAround += soupValue;
                            byte soupBytes = saveSoupAsByte(soupValue);
                            if (SState.mySoupMap[point.x][point.y] != soupBytes) {
                                SState.markSoup(point, soupBytes);
                            }
                            if (((soupBytes > UBlockchain.UNSIGNED_BYTE_MIN_VALUE && SState.blockchainSoupMap[x][y] == UBlockchain.UNSIGNED_BYTE_MIN_VALUE) ||
                                    (soupBytes == UBlockchain.UNSIGNED_BYTE_MIN_VALUE && soupBytes != SState.blockchainSoupMap[x][y])) &&
                                    newSoupLocations.size() <= 8) {
                                // todo if it's not on the "state" map say to blockchain that there're a soup
                                newSoupLocations.add(new MSoupLocation(point, soupBytes));
                            }
                        }
                    }
                }
            }
        }
        return new UTuple2<>(newSoupLocations, soupAround);
    }

    static void sendNewSoupLocationsToBlockchain(List<MSoupLocation> newSoupLocations) throws GameActionException {
        System.out.printf("Send %d new soup locations to the blockchain\n", newSoupLocations.size());
        while (newSoupLocations.size() > 0) {
            // take only first 8 messages to not get buffer overflow
            List<MSoupLocation> takeLocations = newSoupLocations.subList(0, Math.min(8, newSoupLocations.size()));
            int[] txData = UBlockchain.messageToTXData(new MSoupLocations(takeLocations));
            int fee = UBlockchain.bestFee();
            if (fee > 0) {
                GS.c.submitTransaction(txData, fee);
            }
            newSoupLocations = newSoupLocations.subList(Math.min(8, newSoupLocations.size()), newSoupLocations.size());
        }
    }
}
