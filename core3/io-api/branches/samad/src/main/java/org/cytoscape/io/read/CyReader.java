package org.cytoscape.io.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * The basic input interface that specifies what is to be read and when it is to
 * be read. This interface should be extended by other interfaces to provide
 * access to the data that gets read. One class can then implement multiple
 * CyReader interfaces to support reading files that contain multiple types of
 * data (like networks that contain both attribute and view model information).
 * 
 */
public interface CyReader {

	/**
	 * Calling this method will initiate reading of the input specified in the
	 * {@link CyReader#setInput(InputStream is)}. This method will return once
	 * the data has been read and will(?) throw an exception otherwise.
	 * <p>
	 * This should probably throw an {@link java.io.IOException}
	 */
	public Map<Class<?>, Object> read() throws IOException;

	public void setInputStream(InputStream inputStream);

	/**
	 * Cancels reading if a thread is waiting for the completion of
	 * <code>CyReader.read()</code>.
	 * <code>CyReader.read()</code> will prematurely terminate by
	 * throwing an <code>IOException</code>.
	 */
	public void cancel();
}
