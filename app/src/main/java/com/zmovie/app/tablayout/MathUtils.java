package com.zmovie.app.tablayout;

/**
 * Created by owen on 16/9/8.
 */
public class MathUtils {

    static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }
    
}
