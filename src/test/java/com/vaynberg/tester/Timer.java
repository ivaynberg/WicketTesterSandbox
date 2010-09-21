package com.vaynberg.tester;

import org.apache.wicket.util.time.Duration;

public class Timer {
	private final String name;
	private long start;
	private long stop;

	public Timer(String name) {
		this.name = name;
		reset();
	}

	public void stop() {
		stop = System.currentTimeMillis();
	}

	public void reset() {
		start = System.currentTimeMillis();
		stop = -1;
	}

	public Duration elapsed() {
		if (stop >= 0) {
			return Duration.milliseconds(stop - start);
		} else {
			return Duration.milliseconds(System.currentTimeMillis() - start);
		}
	}

	public String toString() {
		return name + ": " + elapsed();
	}

}
