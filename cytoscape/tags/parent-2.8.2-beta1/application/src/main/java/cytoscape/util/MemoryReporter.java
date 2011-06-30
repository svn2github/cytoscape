/**
 *  @author Johannes Ruscheinski
 */
package cytoscape.util;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;



/**
 *  A class used to track JVM memory usage.
 */
public class MemoryReporter {
	/**
	 *  @return the actually used memory in bytes
	 */
	public static long getUsedMemory() {		
		final List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
		long used = 0;
		for (final MemoryPoolMXBean bean : beans)
			used += bean.getUsage().getUsed();
				
		return used;
	}

	/**
	 *  @return the maximum amount of memory in bytes that can be used for memory management.
	 *          If not available, -1 will be returned instead
	 *
	 *  "This amount of memory is not guaranteed to be available for memory management if it is
	 *   greater than the amount of committed memory. The Java virtual machine may fail to
	 *   allocate memory even if the amount of used memory does not exceed this maximum size."
	 */
	public static long getMaxMemory() {		
		final List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
		long max = 0;
		for (final MemoryPoolMXBean bean : beans) {
			final long beanMax = bean.getUsage().getMax();
			if (beanMax == -1L)
				return  -1L;

			max += beanMax;
		}

		return max;
	}

	/**
	 *  @return the "committed" amount of memory for the current JVM instance in bytes
	 *
	 *  "This amount of memory is guaranteed for the Java virtual machine to use."
	 */
	public static long getCommittedMemory() {		
		final List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
		long committed = 0;
		for (final MemoryPoolMXBean bean : beans)
			committed += bean.getUsage().getCommitted();
				
		return committed;
	}
}
