package com.owen.tvrecyclerview.utils;

/**
 * Created by owen on 2017/6/27.
 */

public class MathUtil {
    /**
     * 求两个最大公约数
     * 辗转相除是用大的除以小的。如果n小于m，第一次相当n与m值交换
     * @param n 
     * @param m 
     * @return
     */
    public static int commonDivisor(int n, int m) {
        while (n % m != 0) {
            int temp = n % m;
            n = m;
            m = temp;
        }
        return m;
    }

    /**
     * 求两个数最小公倍数
     * @param n
     * @param m
     * @return
     */
    public static int commonMultiple(int n, int m) {
        return n * m / commonDivisor(n, m);
    }

    /**
     * 求多个数的最小公倍数
     * 参考http://blog.sina.com.cn/s/blog_676370130101b66r.html
     * @param a
     * @return
     */
    public static int commonMultiple(int[] a) {
        int value = a[0];
        for (int i = 1; i < a.length; i++) {
            value = commonMultiple(value, a[i]);
        }
        return value;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
