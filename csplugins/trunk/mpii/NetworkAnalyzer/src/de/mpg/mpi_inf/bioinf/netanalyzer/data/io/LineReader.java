package de.mpg.mpi_inf.bioinf.netanalyzer.data.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for reading text streams one line at a time.
 * 
 * @author Yassen Assenov
 */
public interface LineReader extends Closeable {

	/**
	 * Closes the stream. Once the stream is closed, further invocations of {@link #readLine()}
	 * cause <code>IOException</code>. Closing a previously-closed stream, however, has no effect.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	public void close() throws IOException;

	/**
	 * Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'),
	 * a carriage return ('\r'), or a carriage return followed immediately by a line feed.
	 * 
	 * @return A <code>String</code> containing the contents of the line, not including any
	 * line-termination characters; or <code>null</code> if the end of the stream has been reached.
	 * @throws IOException If an I/O error occurs.
	 */
	public String readLine() throws IOException;

	/**
	 * Checks whether this stream is ready to be read.
	 * 
	 * @return <code>true</code> if the next invocation of {@link #readLine()} is guaranteed not to
	 * block for input, <code>false</code> otherwise. Note that returning <code>false</code> does
	 * not guarantee that the next read will block.
	 * @throws IOException If an I/O error occurs.
	 */
	public boolean ready() throws IOException;
}
