package mybot;

public class UDebug {
    static void drawBuildingCarryingSoup() {
        System.out.printf("There're %d soup on me\n", GS.c.getSoupCarrying());
        int red = Math.min(255, (int) (255 * ((double)GS.c.getSoupCarrying() / 1000)));
        int green = Math.max(0, (int) (255 - 255 * ((double)GS.c.getSoupCarrying() / 1000)));

        GS.c.setIndicatorDot(GS.c.getLocation(), red, green, 0);
    }
}
