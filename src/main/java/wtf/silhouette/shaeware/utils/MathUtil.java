package wtf.silhouette.shaeware.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public final class MathUtil {

    private static final Random random = new Random();

    public static double wrappedDifference(double number1, double number2) {
        return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0), Math.abs(number2 - 360) - Math.abs(number1 - 0)));
    }

    public static int nextInt(int min, int max) {
        return min + random.nextInt((max - min) + 1);
    }

    public static double nextDouble(double min, double max) {
        return min + (random.nextDouble() * (max - min));
    }

    public static float nextFloat(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }
}
