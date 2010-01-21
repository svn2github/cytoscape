package cytoscape.launcher;


class MemSettings {
	private final String threadStackSize;
	private final String memoryAllocationPoolMaximumSize;

	MemSettings(final String threadStackSize, final String memoryAllocationPoolMaximumSize) {
		this.threadStackSize = threadStackSize;
		this.memoryAllocationPoolMaximumSize = memoryAllocationPoolMaximumSize;
	}

	String getThreadStackSize() { return threadStackSize; }
	String getMemoryAllocationPoolMaximumSize() { return memoryAllocationPoolMaximumSize; }
}
