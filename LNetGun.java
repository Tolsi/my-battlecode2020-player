package mybot;

import battlecode.common.GameActionException;

public strictfp class LNetGun {
    static void run() throws GameActionException {
        if (UBlockchain.sendWhatICreated()) {
            System.out.println("Write to blockchain that I was created");
        }
    }
}
