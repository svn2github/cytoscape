package cytoscape.data.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cytoscape.task.TaskMonitor;
import cytoscape.util.FileUtil;
import cytoscape.util.PercentUtil;

public class NNFReader extends AbstractGraphReader {

	// Optional comments start with this character and extend to the end of line.
	private static final char COMMENT_CHAR = '#';
	
	private final NNFParser parser;

	// TODO: move to parent and remove from siblings
	private final InputStream inputStream;
	private TaskMonitor taskMonitor;
	private PercentUtil percentUtil;

	/**
	 * Creates an interaction reader based on the string file name.
	 * 
	 * @param filename
	 *            The filename that contains the interaction data to be read.
	 */
	public NNFReader(String filename) {
		this(filename, null);
	}

	/**
	 * Creates an interaction reader based on the string file name.
	 * 
	 * @param filename
	 *            The filename that contains the interaction data to be read.
	 * @param monitor
	 *            An optional task monitor. May be null.
	 */
	public NNFReader(String filename, TaskMonitor monitor) {
		this(FileUtil.getInputStream(filename), filename);
		this.taskMonitor = monitor;
	}

	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 * 
	 * @param is
	 *            Input stream of GML file,
	 * 
	 */
	public NNFReader(final InputStream is, final String name) {
		super(name);
		this.inputStream = is;
		this.parser = new NNFParser();
	}

	/**
	 * Sets the task monitor we want to use
	 * 
	 * @param monitor
	 *            the TaskMonitor to use
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}

	public void read() throws IOException {

		// Create buffered reader from given InputStream
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));

		String line;
		boolean rootNetworkHasBeenCreated = false;
		try {
			while ((line = in.readLine()) != null) {
				line = processComment(line);
				if (line.length() == 0)
					continue; // Empty line => nothing to do!

				rootNetworkHasBeenCreated = true;
//				parser.parse(line);
			}
		} finally {
			in.close();
		}
		
		if (!rootNetworkHasBeenCreated) {
			throw new IOException("Input NNF file is empty.");
		}

	}
	
	
	public int[] getNodeIndicesArray() {
		return null;
	}
	
	
	public int[] getEdgeIndicesArray() {
		return null;
	}
	
	private String processComment(String line) {
		final int hashPos = line.indexOf(COMMENT_CHAR);
		if (hashPos != -1) {
			line = line.substring(0, hashPos);
		}
		return line.trim();
	}
}
