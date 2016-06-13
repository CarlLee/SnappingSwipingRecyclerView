package com.carl.recyclerview;

/**
 * Utility class for common math operations
 * @author carl
 */
public abstract class MathUtils {

    /**
     * Linearly interpolates a float value from start to end for given progress
     * @param start the value to start with
     * @param end the value when progress reaches 1.0
     * @param progress a value between 0.0~1.0 to indicate the progress of interpolation
     * @return the result value
     */
    public static float lerp(float start, float end, float progress) {
        return start - progress * (start - end);
    }

    /**
     * Linearly interpolates a double value from start to end for given progress
     * @param start the value to start with
     * @param end the value when progress reaches 1.0
     * @param progress a value between 0.0~1.0 to indicate the progress of interpolation
     * @return the result value
     */
    public static double lerp(double start, double end, double progress) {
        return start - progress * (start - end);
    }
}
