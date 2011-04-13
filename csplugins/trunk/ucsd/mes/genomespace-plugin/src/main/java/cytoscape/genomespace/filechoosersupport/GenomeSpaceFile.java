package cytoscape.genomespace.filechoosersupport;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public final class GenomeSpaceFile extends File {
	private final GSFileMetadata fileMetadata;
	private final DataManagerClient dataManagerClient;
	private final String rootDirectory;
	private final String canonicalPath;

	public GenomeSpaceFile(final GSFileMetadata fileMetadata,
			       final DataManagerClient dataManagerClient,
			       final String rootDirectory)
	{
		super(GenomeSpaceFile.getCanonicalPathname(fileMetadata, rootDirectory));
		this.fileMetadata = fileMetadata;
		this.dataManagerClient = dataManagerClient;
		this.rootDirectory = rootDirectory;
		this.canonicalPath =
			GenomeSpaceFile.getCanonicalPathname(fileMetadata, rootDirectory);
	}

	/** Strips "rootDirectory" from the beginning of "noncanonicalPath" and returns the rest after
	 *  ensuring that the rest starts with a leading slash.
	 */
        private static String getCanonicalPathname(final GSFileMetadata fileMetadata,
						   final String rootDirectory)
	{
		final String noncanonicalPath =
			fileMetadata.isDirectory() ? fileMetadata.getPath()
			                           : fileMetadata.getPath() + "/" + fileMetadata.getName();
                if (!noncanonicalPath.startsWith(rootDirectory))
                        throw new IllegalArgumentException("in getCanonicalPathname: noncanonicalPath="
                                                           + noncanonicalPath
                                                           + " does not start with rootDirectory="
                                                           + rootDirectory + "!");

                String canonicalPath = noncanonicalPath.substring(rootDirectory.length());
                if (!canonicalPath.startsWith("/"))
			canonicalPath = "/" + canonicalPath;
		if (canonicalPath.length() == 1)
			return canonicalPath;
		return canonicalPath.endsWith("/") ? canonicalPath.substring(0, canonicalPath.length() - 1)
		                                   : canonicalPath;
        }

	private static String getParentDirectory(final String canonicalPath) {
		if (canonicalPath.length() == 1)
			return null;

		final int lastSlashPos = canonicalPath.lastIndexOf("/");
		return canonicalPath.substring(0, lastSlashPos);
	}

	private static String getBaseName(final String canonicalPath) {
		if (canonicalPath.length() == 1)
			return "";

		final int lastSlashPos = canonicalPath.lastIndexOf("/");
		return canonicalPath.substring(lastSlashPos + 1);
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
		return canonicalPath.compareTo(pathname.getPath());
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
		if (o instanceof GenomeSpaceFile) {
			final GenomeSpaceFile other = (GenomeSpaceFile)o;
			return other.fileMetadata.getPath().equals(fileMetadata.getPath());
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
		return canonicalPath;
	}

	@Override
	public File getCanonicalFile() throws IOException {
		return this;
	}

	@Override
	public String getAbsolutePath() {
		return canonicalPath;
	}

	@Override
	public long getFreeSpace() {
		throw new UnsupportedOperationException("don't know how to get free space from GenomeSpace!");
	}

	@Override
	public String getName() {
		return GenomeSpaceFile.getBaseName(canonicalPath);
	}

	@Override
	public String getParent() {
		return GenomeSpaceFile.getParentDirectory(canonicalPath);
	}

	@Override
	public File getParentFile() {
		final String parentDir = GenomeSpaceFile.getParentDirectory(canonicalPath);
		if (parentDir == null)
			return null;

		final String serverParentDir =
			fileMetadata.isDirectory() ? GenomeSpaceFile.getParentDirectory(fileMetadata.getPath())
			                           : fileMetadata.getPath();
		final GSFileMetadata parentMetadata = dataManagerClient.getMetadata(serverParentDir);
		final GenomeSpaceFile parentFile =
			new GenomeSpaceFile(parentMetadata, dataManagerClient, rootDirectory);
		return parentFile;
	}

	@Override
	public String getPath() {
		return canonicalPath;
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
		final Date lastModified = fileMetadata.getLastModified();
		return lastModified == null ? 0 : lastModified.getTime();
	}

	@Override
	public long length() {
		return fileMetadata.getSize();
	}

	@Override
	public String[] list() {
		if (!fileMetadata.isDirectory())
			return null;

		final List<GSFileMetadata> filesMetaData = dataManagerClient.list(fileMetadata).getContents();
		final List<String> fileNames = new ArrayList<String>(filesMetaData.size());
		for (final GSFileMetadata metadata : filesMetaData)
			fileNames.add(GenomeSpaceFile.getCanonicalPathname(metadata, rootDirectory));
		final String[] strings = new String[filesMetaData.size()];
		return fileNames.toArray(strings);
	}

	@Override
	public String[] list(final FilenameFilter filter) {
		throw new UnsupportedOperationException("filtering is currently not impemented for GenomeSpace(1)!");
	}

	@Override
	public synchronized File[] listFiles() {
		if (!fileMetadata.isDirectory())
			return null;

		final List<GSFileMetadata> filesMetaData = dataManagerClient.list(fileMetadata).getContents();
		final List<File> fileNames = new ArrayList<File>(filesMetaData.size());
		for (final GSFileMetadata metadata : filesMetaData)
			fileNames.add(new GenomeSpaceFile(metadata, dataManagerClient, rootDirectory));
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