package org.eclipse.emf.emfstore.performance.test;

import java.util.Arrays;

public class Calculate {

	public static double max(double[] arr) {
		double max = Double.MIN_VALUE;
		for (final double x : arr) {
			if (x > max) {
				max = x;
			}
		}
		return max;
	}

	public static double average(double[] arr) {
		double sum = 0.0;
		for (final double x : arr) {
			sum += x;
		}
		return (int) (sum / arr.length * 1000.0) / 1000.0;
	}

	public static double mean(double[] arr) {
		Arrays.sort(arr);
		final int ind = arr.length / 2 - 1 + arr.length % 2;
		return arr[ind];
	}

	public static double min(double[] arr) {
		double min = Double.MAX_VALUE;
		for (final double x : arr) {
			if (x < min) {
				min = x;
			}
		}
		return min;
	}
}
