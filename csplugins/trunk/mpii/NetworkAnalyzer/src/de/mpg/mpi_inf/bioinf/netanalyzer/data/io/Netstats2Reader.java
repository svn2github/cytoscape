package de.mpg.mpi_inf.bioinf.netanalyzer.data.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reader for &quot;.netstats&quot: version 2 files. It uses <code>BufferedReader</code> for
 * accessing the underlying stream.
 * 
 * @author Yassen Assenov
 */
class Netstats2Reader implements LineReader {

	/**
	 * Initializes a new instance of <code>Netstats2Reader</code>.
	 * 
	 * @param aFileReader Reader of the underlying file stream.
	 */
	public Netstats2Reader(FileReader aFileReader) {
		reader = new BufferedReader(aFileReader);
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
		return reader.readLine();
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
	 * Buffered reader of the underlying stream.
	 */
	private BufferedReader reader;
}
