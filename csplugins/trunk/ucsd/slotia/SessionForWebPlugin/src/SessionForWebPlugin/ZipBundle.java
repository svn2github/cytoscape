package SessionForWebPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.HashSet;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class ZipBundle extends Bundle
{
	private ZipOutputStream zipOutput = null;
	private Set<String> entries = new HashSet<String>();
	
	public ZipBundle(File zipFile) throws FileNotFoundException
	{
		this(zipFile, Deflater.DEFAULT_COMPRESSION);
	}

	/**
	 * @param compressionLevel the compression level, from 0 to 9
	 */
	public ZipBundle(File zipFile, int compressionLevel) throws FileNotFoundException
	{
		zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));
		zipOutput.setLevel(compressionLevel);
	}

	public void openEntry(String entry) throws IOException
	{
		entries.add(entry);
		ZipEntry zipEntry = new ZipEntry(entry);
		zipOutput.putNextEntry(zipEntry);
	}

	public boolean hasEntry(String entry)
	{
		return entries.contains(entry);
	}

	public void closeEntry() throws IOException
	{
		zipOutput.closeEntry();
	}

	public OutputStream entryOutputStream() throws FileNotFoundException
	{
		return zipOutput;
	}

	public Writer entryWriter() throws IOException
	{
		// For some reason, OutputStreamWriter does not work
		// That is why I wrote the following anonymous class
		final ZipOutputStream outputStream = zipOutput;
		return new Writer(outputStream)
		{
			public void write(char[] cbuf, int off, int len) throws IOException
			{
				String data = new String(cbuf, off, len);
				byte[] buffer = data.getBytes();
				outputStream.write(buffer, 0, buffer.length);
			}

			public void flush() throws IOException
			{
			}

			public void close() throws IOException
			{
			}
		};
	}

	public void close() throws IOException
	{
		zipOutput.close();
	}
}
