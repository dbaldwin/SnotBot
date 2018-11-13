package com.unmannedairlines.snotbot;

import java.util.Arrays;

/**
 * Created by db on 11/3/18.
 */

public class RollingAverage {

    private int size;
    private double total = 0;
    private int index = 0;
    private double samples[];
    protected double sorted[];

    public RollingAverage(int size) {
        this.size = size;
        samples = new double[size];
        sorted = new double[size];
        for (int i = 0; i < size; i++) samples[i] = 0d;
    }

    public void add(double x) {
        total -= samples[index];
        samples[index] = x;
        sorted[index] = samples [index];
        Arrays.sort(sorted);
        total += x;
        if (++index == size) index = 0;// cheaper than modulus
    }

    public double getAverage() {
        return total / size;
    }

}
