package cytoscape.launcher;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;


class StreamMapper extends Thread {
	final BufferedReader in;
	final PrintStream out;
	final Object mutex;

	StreamMapper(final InputStreamReader in, final PrintStream out, final Object mutex) {
		this.in    = new BufferedReader(in);
		this.out   = out;
		this.mutex = mutex;
	}

	public void run() {
		String line;
		try {
			while ((line = in.readLine()) != null) {
				synchronized(mutex) {
					out.println(line);
					out.flush();
				}
			}
		} catch (final java.io.IOException e) {
			System.err.println("StreamMapper.run(): I/O error!");
		}
	}
}
