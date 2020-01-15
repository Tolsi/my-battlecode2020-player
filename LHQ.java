package mybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;
import battlecode.common.Team;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public strictfp class LHQ {
    static void run() throws GameActionException {
        if (SMap.hqLoc == null) {
            Map<Team, List<MMapUpdate>> adds = new HashMap<>();
            adds.put(GS.c.getTeam(), Collections.singletonList(new MMapUpdate(GS.c.getLocation(), RobotType.HQ)));
            int[] txData = UBlockchain.messageToTXData(new MMapUpdates((byte) 0, adds, Collections.emptyMap()));
            // todo fee? ges last block fees
            if (GS.c.canSubmitTransaction(txData, 10)) {
                GS.c.submitTransaction(txData, 10);
            }
        }
        for (Direction dir : UDirections.all)
            GS.tryBuild(RobotType.MINER, dir);
    }
}
