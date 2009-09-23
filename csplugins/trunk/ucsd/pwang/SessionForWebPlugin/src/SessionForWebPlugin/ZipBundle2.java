package SessionForWebPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.HashSet;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class ZipBundle2 extends Bundle
{
	private ZipOutputStream zipOutput = null;
	private Set<String> entries = new HashSet<String>();
	
	public ZipBundle2(File zipFile) throws FileNotFoundException
	{
		this(zipFile, Deflater.DEFAULT_COMPRESSION);
	}

	/**
	 * @param compressionLevel the compression level, from 0 to 9
	 */
	public ZipBundle2(File zipFile, int compressionLevel) throws FileNotFoundException
	{
		zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));
		zipOutput.setLevel(compressionLevel);
	}

	public void openEntry(String entry) throws IOException // not used
	{
		entries.add(entry);
		ZipEntry zipEntry = new ZipEntry(entry);
		zipOutput.putNextEntry(zipEntry);
	}

	public void openEntry(String subDirectory, String entry) throws IOException
	{
		entry = subDirectory + "/"+ entry;
		entries.add(entry);
		ZipEntry zipEntry = new ZipEntry(entry);
		zipOutput.putNextEntry(zipEntry);
	}

	public void putEntry(File pFile, String entry) throws IOException
	{
		FileInputStream in = new FileInputStream(pFile);
		entries.add(entry);
		ZipEntry zipEntry = new ZipEntry(entry);
		zipOutput.putNextEntry(zipEntry);
		
		byte[] buf = new byte[1024];
		// Transfer bytes from file to the ZIP file
		int len;
		while ((len=in.read(buf))>0){
			zipOutput.write(buf,0,len);
		}
		in.close();
		zipOutput.closeEntry();
	}
	
	
	public boolean hasEntry(String entry) // not used
	{
		return entries.contains(entry);
	}

	public boolean hasEntry(String subDirectory, String entry)
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

	public static String thumbnailFile(String network, String format)
	{
		//return network + "_thumbnail." + format;
		//int lastIndex = network.lastIndexOf(".sif");
		//network = network.substring(0, lastIndex);
		return network + "."+format;

	}

}
