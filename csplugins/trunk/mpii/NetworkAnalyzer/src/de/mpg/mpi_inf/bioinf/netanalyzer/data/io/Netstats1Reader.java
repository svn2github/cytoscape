package de.mpg.mpi_inf.bioinf.netanalyzer.data.io;

import java.io.IOException;

/**
 * Reader for &quot;.netstats&quot: version 1 files. It is a wrapper around a
 * {@link Netstats2Reader} instance, providing conversion wherever necessary.
 * 
 * @author Yassen Assenov
 */
class Netstats1Reader implements LineReader {

	/**
	 * Initializes a new instance of <code>Netstats1Reader</code>.
	 * 
	 * @param aReader Successfully initialized Netstats version 2 reader.
	 * @throws NullPointerException If <code>aReader</code> is <code>null</code>.
	 */
	public Netstats1Reader(Netstats2Reader aReader) {
		if (aReader == null) {
			throw new NullPointerException();
		}
		reader = aReader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.data.io.LineReader#close()
	 */
	public void close() throws IOException {
		reader.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.data.io.LineReader#readLine()
	 */
	public String readLine() throws IOException {
		String line = reader.readLine();
		if (line != null) {
			if (line.startsWith("splDist IntHistogram")) {
				line = "splDist LongHistogram" + line.substring(20);
			}
		}
		return line;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.data.io.LineReader#ready()
	 */
	public boolean ready() throws IOException {
		return reader.ready();
	}

	/**
	 * Underlying Netstats version 2 reader.
	 */
	private Netstats2Reader reader;
}
