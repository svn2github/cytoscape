package de.mpg.mpi_inf.bioinf.netanalyzer.data.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class providing helper methods for stream manipulation.
 * 
 * @author Yassen Assenov
 */
public abstract class IOUtils {

	/**
	 * Reads the contents of the given stream as a <code>String</code> and closes the stream.
	 * 
	 * @param aStream
	 *            Stream to be read.
	 * @return The text read from the stream.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs while reading from or while closing the stream.
	 * @throws NullPointerException
	 *             If <code>aStream</code> is <code>null</code>.
	 */
	public static final String readFile(InputStream aStream) throws IOException {
		final StringBuilder text = new StringBuilder();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(aStream));
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				if (text.length() != 0) {
					text.append("\n");
				}
				text.append(line);
			}
		} catch (IOException ex) {
			closeStream(reader);
			throw ex;
		}
		reader.close();
		return text.toString();
	}

	/**
	 * Silently closes the given stream.
	 * <p>
	 * This method does not throw an exception in any circumstances.
	 * </p>
	 * 
	 * @param aStream
	 *            Stream to be closed.
	 */
	public static void closeStream(Closeable aStream) {
		try {
			aStream.close();
		} catch (Exception ex) {
			// Unsuccessful attempt to close the stream; ignore
		}
	}
}
