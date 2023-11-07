package net.heyimamethyst.fairyfactions.util;

public class MathUtil
{
    public static double roundWithPercision (double value, int precision)
    {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static boolean isNegative(double d)
    {
        return Double.doubleToRawLongBits(d) < 0;
    }
}
