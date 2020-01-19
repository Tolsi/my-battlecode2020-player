package mybot;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public strictfp class LDeliveryDrone {
    private static LTask currentTask = null;

    static void run() throws GameActionException {
        if (currentTask == null) {
            currentTask = new LDeliveryDroneMoveLandscapersToHQWallTask();
        }
        if (currentTask.isFinished()) {
            if (currentTask instanceof LDeliveryDroneMoveLandscapersToHQWallTask) {
                currentTask = new LDeliveryDroneRandomAttackUnits();
            }
        } else {
            currentTask.step();
        }
    }
}
