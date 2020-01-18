package mybot;

public class LUtils {
    public static final strictfp int distanceSquared(int fromX, int fromY, int toX, int toY) {
        int dx = fromX - toX;
        int dy = fromY - toY;
        return dx * dx + dy * dy;
    }

    public static float getWaterLevel(int roundNumber) {
        double x = roundNumber;
        return (float)(Math.exp(0.0028D * x - 1.38D * Math.sin(0.00157D * x - 1.73D) + 1.38D * Math.sin(-1.73D)) - 1.0D);
    }
    // todo миссии? Идти и строить туда-то
}
