package SessionExporterPlugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class DirectoryBundle extends Bundle
{
	private File directory;
	private File currentEntry;
	private OutputStream currentStream;
	private Writer currentWriter;
	
	public DirectoryBundle(File directory)
	{
		this.directory = directory;
		currentEntry = null;
		currentStream = null;
		currentWriter = null;
	}

	public void openEntry(String entry) throws IOException
	{
		closeEntry();
		currentEntry = new File(directory, entry);
	}

	public boolean hasEntry(String entry)
	{
		File file = new File(directory, entry);
		return file.exists();
	}

	public void closeEntry() throws IOException
	{
		currentEntry = null;

		if (currentStream != null)
		{
			currentStream.close();
			currentStream = null;
		}

		if (currentWriter != null)
		{
			currentWriter.close();
			currentWriter = null;
		}
	}

	public OutputStream entryOutputStream() throws FileNotFoundException
	{
		if (currentEntry == null)
			return null;

		if (currentStream == null)
			currentStream = new FileOutputStream(currentEntry);

		return currentStream;
	}

	public Writer entryWriter() throws IOException
	{
		if (currentEntry == null)
			return null;

		if (currentWriter == null)
			currentWriter = new FileWriter(currentEntry);

		return currentWriter;
	}

	public void close() throws IOException
	{
		closeEntry();
	}
}
