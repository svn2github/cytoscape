package org.idekerlab.PanGIAPlugin.utilities;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;

public class MemoryReporter {

	public static void reportMemoryUsage() {
		List<MemoryPoolMXBean> mp = ManagementFactory.getMemoryPoolMXBeans();

		long used = 0;
		long committed = 0;
		long mmax = 0;

		for (MemoryPoolMXBean mpb : mp) {
			used += mpb.getUsage().getUsed();
			committed += mpb.getUsage().getCommitted();
			mmax += mpb.getUsage().getMax();
		}

		System.out.println("Memory Usage: " + used / 1048576 + "M/" + committed
				/ 1048576 + "M/" + mmax / 1048576 + "M, " + (float) used / mmax
				* 100 + "%");
	}
}
