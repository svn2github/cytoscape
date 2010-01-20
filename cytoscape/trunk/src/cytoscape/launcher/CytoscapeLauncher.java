package cytoscape.launcher;


import java.util.Properties;
import java.io.FileInputStream;


public class CytoscapeLauncher {
	static class MemSettings {
		private final String threadStackSize;
		private final String memoryAllocationPoolMaximumSize;

		MemSettings(final String threadStackSize, final String memoryAllocationPoolMaximumSize) {
			this.threadStackSize = threadStackSize;
			this.memoryAllocationPoolMaximumSize = memoryAllocationPoolMaximumSize;
		}

		String getThreadStackSize() { return threadStackSize; }
		String getMemoryAllocationPoolMaximumSize() { return memoryAllocationPoolMaximumSize; }
	}


	final static String VMCONFIG_FILENAME = "cytoscape.vmconfig";


	static MemSettings getMemSettings() {
		final Properties props = new Properties();
		try {
			props.load(new FileInputStream(VMCONFIG_FILENAME));
		} catch (final java.io.IOException e) {
			System.err.println("Failed to load properties from \"" + VMCONFIG_FILENAME + "\"!");
			System.exit(-1);
		}

		final String threadStackSize = props.getProperty("threadStackSize");
		if (threadStackSize == null) {
			System.err.println("Can't find \"threadStackSize\" property in \"" + VMCONFIG_FILENAME + "\"!");
			System.exit(-1);
		}

		final String memoryAllocationPoolMaximumSize = props.getProperty("memoryAllocationPoolMaximumSize");
		if (memoryAllocationPoolMaximumSize == null) {
			System.err.println("Can't find \"memoryAllocationPoolMaximumSize\" property in \"" + VMCONFIG_FILENAME + "\"!");
			System.exit(-1);
		}

		return new MemSettings(threadStackSize, memoryAllocationPoolMaximumSize);
	}


	static public void main(final String args[]) {
		final boolean verbose = args.length > 0 && args[0].equals("-verbose");

		final MemSettings memSettings = getMemSettings();

		final String[] execArgs = new String[6];
		execArgs[0] = "java";
		execArgs[1] = "-Xss" + memSettings.getThreadStackSize();
		execArgs[2] = "-Xmx" + memSettings.getMemoryAllocationPoolMaximumSize();
		execArgs[3] = "-cp";
		execArgs[4] = System.getProperty("user.dir");;
		execArgs[5] = "Cytoscape";

		if (verbose) {
			System.out.print("Attempting to run: ");
			for (final String arg : execArgs)
				System.out.print(arg + " ");
			System.out.println();
		}

		try {
			final Process child = Runtime.getRuntime().exec(execArgs);
			/*
			  int exitCode = 0;
			  try {
			  exitCode = child.waitFor();
			  } catch (final java.lang.InterruptedException e) {
			  System.out.println("waitFor() was interrupted: ");
			  System.out.println(e.toString());
			  System.exit(-1);
			  }

			  if (exitCode != 0) {
			  System.out.println("Child failed w/ exit code " + exitCode);
			  System.exit(-1);
			  }
			*/
			java.io.InputStream err = child.getInputStream();
			int c;
			while ((c = err.read()) != -1)
				System.out.print((char)c);
			err.close();

			System.exit(0);
		} catch (final java.io.IOException e) {
			System.out.println("Failed to execute subprocess:");
			System.out.println(e.toString());
			System.exit(-1);
		}
	}
}
