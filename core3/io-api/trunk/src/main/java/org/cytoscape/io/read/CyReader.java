package org.cytoscape.io.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

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
	public void read() throws IOException;

	public void setInputStream(InputStream is);

	/**
	 * Client classes get the actual read data objects by this method. This is a
	 * type-safe heterogeneous container pattern.
	 * 
	 * If client code use this method before read(), IllegalStateException will be thrown.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T getReadData(Class<T> type) throws IllegalStateException, IllegalArgumentException;

	public Set<Class<?>> getSupportedDataTypes();
}
