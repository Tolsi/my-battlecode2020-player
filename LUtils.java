package mybot;

public class LUtils {
    public static final strictfp int distanceSquared(int fromX, int fromY, int toX, int toY) {
        int dx = fromX - toX;
        int dy = fromY - toY;
        return dx * dx + dy * dy;
    }
}
