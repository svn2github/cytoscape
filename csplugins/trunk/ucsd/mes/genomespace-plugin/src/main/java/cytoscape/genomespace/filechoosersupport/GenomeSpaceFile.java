package cytoscape.genomespace.filechoosersupport;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public final class GenomeSpaceFile extends File {
	private final GSFileMetadata fileMetadata;
	private final DataManagerClient dataManagerClient;
	private final GenomeSpaceFile parent;

	public GenomeSpaceFile(final GSFileMetadata fileMetadata,
			       final DataManagerClient dataManagerClient,
			       final GenomeSpaceFile parent)
	{
		super(fileMetadata.getPath() + "/" + fileMetadata.getName());
		this.fileMetadata = fileMetadata;
		this.dataManagerClient = dataManagerClient;
		this.parent = parent;
	}

	@Override
	public boolean canExecute() {
		return false;
	}

	@Override
	public boolean canRead() {
		return true;
	}

	@Override
	public boolean canWrite() {
		return true;
	}

	@Override
	public int compareTo(final File pathname) {
		try {
			return (fileMetadata.getPath() + "/" + fileMetadata.getName())
				.compareToIgnoreCase(pathname.getCanonicalPath());
		} catch (final IOException e) {
			return 0;
		}
	}

	@Override
	public boolean createNewFile() {
		throw new UnsupportedOperationException("currently can't create new GenomeSpace files!");
	}

	@Override
	public boolean delete() {
		throw new UnsupportedOperationException("currently can't delete GenomeSpace files!");
	}

	@Override
	public void deleteOnExit() {
		throw new UnsupportedOperationException("currently can't delete GenomeSpace files when exiting the JVM!");
	}

	@Override
	public boolean equals(final Object o) {
if ((o instanceof File) && !(o instanceof GenomeSpaceFile))System.err.println("Strange, we have a File that is not a GenomeSpaceFile ("+o);
		if (o instanceof GenomeSpaceFile) {
			final GenomeSpaceFile other = (GenomeSpaceFile)o;
			return other.fileMetadata.getName().equals(fileMetadata.getName())
			       && other.fileMetadata.getPath().equals(fileMetadata.getPath());
		} else
			return false;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public File getAbsoluteFile() {
		return this;
	}

	@Override
	public String getCanonicalPath() throws IOException {
		return fileMetadata.getPath() + "/" + fileMetadata.getName();
	}

	@Override
	public File getCanonicalFile() throws IOException {
		return this;
	}

	@Override
	public String getAbsolutePath() {
		return fileMetadata.getPath() + "/" + fileMetadata.getName();
	}

	@Override
	public long getFreeSpace() {
		throw new UnsupportedOperationException("don't know how to get free space from GenomeSpace!");
	}

	@Override
	public String getName() {
		return fileMetadata.getName();
	}

	@Override
	public String getParent() {
		System.err.println("oooooooooooooooooooooooooooooooooo call to GenomeSpaceFile.getParent() -> "+fileMetadata.getPath());
		return fileMetadata.getPath();
	}

	@Override
	public File getParentFile() {
System.err.println("oooooooooooooooooooooooooooooooooo call to GenomeSpaceFile.getParentFile(), this="+this+", parent="+parent);
		return parent;
	}

	@Override
	public String getPath() {
		return fileMetadata.getPath() + "/" + fileMetadata.getName();
	}

	@Override
	public long getTotalSpace() {
		throw new UnsupportedOperationException("don't know how to get the total space from GenomeSpace!");
	}

	@Override
	public long getUsableSpace() {
		throw new UnsupportedOperationException("don't know how to get the total space from GenomeSpace!");
	}

	@Override
	public int hashCode() {
		return fileMetadata.hashCode();
	}

	@Override
	public boolean isAbsolute() {
		return true;
	}

	@Override
	public boolean isDirectory() {
		return fileMetadata.isDirectory();
	}

	@Override
	public boolean isFile() {
		return !fileMetadata.isDirectory();
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public long lastModified() {
		return fileMetadata.getLastModified().getTime();
	}

	@Override
	public long length() {
		return fileMetadata.getSize();
	}

	@Override
	public String[] list() {
System.err.println("****************************** Entering list()");
		if (!fileMetadata.isDirectory())
			return null;

		final List<GSFileMetadata> filesMetaData = dataManagerClient.list(fileMetadata).getContents();
System.err.println("****************************** data manager returned " + filesMetaData.size() + " files");
		final List<String> fileNames = new ArrayList<String>(filesMetaData.size());
		for (final GSFileMetadata metadata : filesMetaData)
{System.err.println("****************************** Adding file name: " + metadata.getName());
			fileNames.add(metadata.getName());
}
		final String[] strings = new String[filesMetaData.size()];
		return fileNames.toArray(strings);
	}

	@Override
	public String[] list(final FilenameFilter filter) {
		throw new UnsupportedOperationException("filtering is currently not impemented for GenomeSpace(1)!");
	}

	@Override
	public File[] listFiles() {
System.err.println("+++++++++++++++ Entering listFiles()");
		if (!fileMetadata.isDirectory())
			return null;

		final List<GSFileMetadata> filesMetaData = dataManagerClient.list(fileMetadata).getContents();
System.err.println("+++++++++++++++ got " + filesMetaData + " files from the data manager");
		final List<File> fileNames = new ArrayList<File>(filesMetaData.size());
		for (final GSFileMetadata metadata : filesMetaData)
			fileNames.add(new GenomeSpaceFile(metadata, dataManagerClient, parent));
		final File[] files = new GenomeSpaceFile[filesMetaData.size()];
		return fileNames.toArray(files);
	}

	@Override
	public File[] listFiles(final FilenameFilter filter) {
		throw new UnsupportedOperationException("filtering is currently not impemented for GenomeSpace(2)!");
	}

	@Override
	public boolean mkdir() {
		throw new UnsupportedOperationException("directory creation is currently not impemented for GenomeSpace!");
	}

	@Override
	public boolean mkdirs() {
		throw new UnsupportedOperationException("recursive directory creation is currently not impemented for GenomeSpace!");
	}

	@Override
	public boolean renameTo(final File dest) {
		throw new UnsupportedOperationException("file renaming is currently not supported for GenomeSpace!");
	}

	@Override
	public boolean setExecutable(final boolean executable) {
		return false;
	}

	@Override
	public boolean setExecutable(final boolean executable, final boolean ownerOnly) {
		return false;
	}

	@Override
	public boolean setLastModified(final long time) {
		return false;
	}

	@Override
	public boolean setReadable(final boolean readable) {
		return false;
	}

	@Override
	public boolean setReadable(final boolean readable, final boolean ownerOnly) {
		return false;
	}

	@Override
	public boolean setReadOnly() {
		return false;
	}

	@Override
	public boolean setWritable(final boolean writable) {
		return false;
	}

	@Override
	public boolean setWritable(final boolean writable, final boolean ownerOnly) {
		return false;
	}

	@Override
	public String toString() {
		return getPath();
	}

	@Override
	public URI toURI() {
		throw new UnsupportedOperationException("currently can't generate URIs for GenomeSpace files!");
	}
}