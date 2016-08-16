package org.eclipse.emf.emfstore.performance.test;

/**
 * Class that measures memory, used during some operation(s) continuously and
 * returns maximal value at the end.
 */
public class MemoryMeter extends Thread {
	/**
	 * Period to wait (in milliseconds) between memory measurements.
	 **/
	private static final int MEASUREMENT_PERIOD = 250;

	private boolean stop = false;
	private boolean active;
	private volatile long maxUsedMemory;

	@Override
	public void run() {
		startMeasurements();
		try {
			while (!stop) {
				if (active) {
					long usedMemory = Runtime.getRuntime().totalMemory()
						- Runtime.getRuntime().freeMemory();
					if (usedMemory > maxUsedMemory) {
						maxUsedMemory = usedMemory;
					}
				}
				Thread.sleep(MEASUREMENT_PERIOD);
			}
		} catch (InterruptedException e) {
		}
	}

	public void startMeasurements() {
		active = true;
		maxUsedMemory = 0;
	}

	public long stopMeasurements() {
		active = false;
		long usedMemory = Runtime.getRuntime().totalMemory()
			- Runtime.getRuntime().freeMemory();
		long curMaxMemory = maxUsedMemory;
		if (usedMemory > curMaxMemory) {
			curMaxMemory = usedMemory;
		}
		return curMaxMemory;
	}

	public void finish() {
		stop = true;
	}
}