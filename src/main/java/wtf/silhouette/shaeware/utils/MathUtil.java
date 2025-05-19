package wtf.silhouette.shaeware.utils;

public final class MathUtil {

  private MathUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static double wrappedDifference(double number1, double number2) {
    return Math.min(Math.abs(number1 - number2), Math.min(Math.abs(number1 - 360) - Math.abs(number2 - 0), Math.abs(number2 - 360) - Math.abs(number1 - 0)));
  }
}
