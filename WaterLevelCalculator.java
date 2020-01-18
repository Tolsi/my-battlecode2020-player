package mybot;

public class WaterLevelCalculator {
//    3257=3252,95
//    3258=3268,94
//    3259=3285,01
    public static void main(String[] args) {
        for (int i = 1; i < 10000; i++) {
            System.out.printf("%d=%.2f\n", i, LUtils.getWaterLevel(i));
        }
    }
}
