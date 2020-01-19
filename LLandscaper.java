package mybot;

import battlecode.common.GameActionException;

public strictfp class LLandscaper {
    private static LTask currentTask = null;

    static void run() throws GameActionException {
        if (currentTask == null) {
            currentTask = new LLandscaperGoToHQWallTask();
        }
        if (currentTask.isFinished()) {
            if (currentTask instanceof LLandscaperGoToHQWallTask) {
                currentTask = new LLandscaperDigHQWallTask();
            }
        } else {
            currentTask.step();
        }
    }
}
