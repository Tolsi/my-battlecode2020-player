package mybot;

import battlecode.common.GameActionException;

public abstract class LTask {
    abstract void step() throws GameActionException;
    abstract boolean isFinished() throws GameActionException ;
}
