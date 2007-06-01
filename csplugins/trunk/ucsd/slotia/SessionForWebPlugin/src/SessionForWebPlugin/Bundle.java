package SessionForWebPlugin;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A Bundle groups a collection of related files together;
 * files are stored as entries in a bundle.
 */
public abstract class Bundle
{
	/**
	 * Opens an entry in the Bundle.
	 * This method prepares the Bundle for a new entry,
	 * but does not guarantee that an entry is
	 * actually exists in the bundle. Calling
	 * either <tt>entryOutputStream()</tt> or
	 * <tt>entryWriter()</tt> will guarantee this.
	 */
	public abstract void openEntry(String entry) throws IOException;

	/**
	 * @return <tt>true</tt> if an entry exists in the Bundle,
	 * <tt>false</tt> otherwise.
	 */
	public abstract boolean hasEntry(String entry);

	/**
	 * Closes an entry.
	 * This <i>must</i> be called after <tt>openEntry()</tt>
	 * and when one is finished with an entry.
	 */
	public abstract void closeEntry() throws IOException;

	/**
	 * Creates an <tt>OutputStream</tt> for writing to an entry.
	 * <i>DO NOT</i> call <tt>close()</tt> on the resulting
	 * <tt>OutputStream</tt>. Call <tt>closeEntry()</tt>
	 * to close the entry.
	 */
	public abstract OutputStream entryOutputStream() throws FileNotFoundException;

	/**
	 * Creates a <tt>Writer</tt> for writing to an entry.
	 * <i>DO NOT</i> call <tt>close()</tt> on the resulting
	 * <tt>Writer</tt>. Call <tt>closeEntry()</tt>
	 * to close the entry.
	 */
	public abstract Writer entryWriter() throws IOException;

	/**
	 * Closes a Bundle.
	 * <i>Note:</i> Failing to call this after all
	 * entries have been added may result in an invalid Bundle.
	 */
	public abstract void close() throws IOException;

	public static String sessionFile()
	{
		return "session.cys";
	}

	public static String indexHTMLFile(int i)
	{
		if (i == 0)
			return "index.html";
		else
			return "index" + i + ".html";
	}

	public static String sifFile(String network)
	{
		return network + ".sif";
	}

	public static String imageFile(String network, String format)
	{
		return network + "." + format;
	}

	public static String thumbnailFile(String network, String format)
	{
		return network + "_thumbnail." + format;
	}

	public static String legendFile(String network, String format)
	{
		return network + "_legend." + format;
	}

	public static String networkHTMLFile(String network)
	{
		return network + ".html";
	}
}
