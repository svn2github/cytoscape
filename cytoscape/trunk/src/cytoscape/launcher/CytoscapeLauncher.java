package cytoscape.launcher;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;


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


	static class StreamMapper extends Thread {
		final BufferedReader in;
		final PrintStream out;

		StreamMapper(final InputStreamReader in, final PrintStream out) {
			this.in  = new BufferedReader(in);
			this.out = out;
		}

		public void run() {
			String line;
			try {
				while ((line = in.readLine()) != null) {
					out.println(line);
					out.flush();
				}
			} catch (final java.io.IOException e) {
				System.err.println("StreamMapper.run(): I/O error!");
			}
		}
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

		final ArrayList<String> execArgs = new ArrayList<String>();
		execArgs.add("java");
		execArgs.add("-Dswing.aatext=true");
		execArgs.add("-Dawt.useSystemAAFontSettings=lcd");
		execArgs.add("-Xss" + memSettings.getThreadStackSize());
		execArgs.add("-Xmx" + memSettings.getMemoryAllocationPoolMaximumSize());
		execArgs.add("-cp");
		execArgs.add(System.getProperty("user.dir"));
		execArgs.add("-jar");
		execArgs.add("cytoscape.jar");
		execArgs.add("cytoscape.CyMain");
		execArgs.add("-p plugins");

		if (verbose) {
			System.err.print("Attempting to run: ");
			for (final String arg : execArgs)
				System.err.print(arg + " ");
			System.err.println();
		}

		try {
			final Process child = Runtime.getRuntime().exec(execArgs.toArray(new String[execArgs.size()]));
			final InputStreamReader childStdout = new InputStreamReader(child.getInputStream());
			final InputStreamReader childStderr = new InputStreamReader(child.getErrorStream());

			final Thread mapStdout = new StreamMapper(childStdout, System.out);
			final Thread mapStderr = new StreamMapper(childStdout, System.err);
			mapStdout.start();
			mapStderr.start();

			try {
				child.waitFor();
				mapStdout.join();
				mapStderr.join();

				// Return the exit code from Cytoscape to the O/S.
				final int exitCode = child.exitValue();
				System.exit(exitCode);
			} catch (final Exception e) {
				System.exit(-1);
			}
		} catch (final java.io.IOException e) {
			System.out.println("Failed to execute subprocess:");
			System.out.println(e.toString());
			System.exit(-1);
		}
	}
}
