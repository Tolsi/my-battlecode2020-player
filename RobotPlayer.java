package mybot;
import battlecode.common.*;

public strictfp class RobotPlayer {

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        GS.c = rc;
        GS.lifespan = 0;
        SMap.init(rc);

        System.out.printf("%d: I'm a %s[%d] and I just got created!\n", rc.getRoundNum(), rc.getType(), rc.getID());
        while (true) {
            GS.lifespan += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.printf("%d: I'm a %s[%d] at %s - STARTED - Spent=%d \n", rc.getRoundNum(), rc.getType(), rc.getID(), rc.getLocation(), Clock.getBytecodeNum());
                if (rc.getRoundNum() > 1) {
                    SMap.update(rc, rc.getRoundNum() - 1);
                }
                System.out.printf("%d: I'm a %s[%d] - after update - Spent=%d\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), Clock.getBytecodeNum());
                switch (rc.getType()) {
                    case HQ:                 LHQ.run();                break;
                    case MINER:              LMiner.run();             break;
                    case REFINERY:           LRefinery.run();          break;
                    case VAPORATOR:          LVaporator.run();         break;
                    case DESIGN_SCHOOL:      LDesignSchool.run();      break;
                    case FULFILLMENT_CENTER: LFulfillmentCenter.run(); break;
                    case LANDSCAPER:         LLandscaper.run();        break;
                    case DELIVERY_DRONE:     LDeliveryDrone.run();     break;
                    case NET_GUN:            LNetGun.run();            break;
                }
                System.out.printf("%d: I'm a %s[%d] at %s - FINISHED! Spent=%d, Countdown=%.1f\n", GS.c.getRoundNum(), GS.c.getType(), GS.c.getID(), GS.c.getLocation(), Clock.getBytecodeNum(), GS.c.getCooldownTurns());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(rc.getType() + " Exception");
            }
        }
    }


}
