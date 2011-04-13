package cytoscape.genomespace.filechoosersupport;


import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public final class GenomeSpaceFileSystemView extends FileSystemView {
	private final DataManagerClient dataManagerClient;
	private GSFileMetadata root;

	public GenomeSpaceFileSystemView(final DataManagerClient dataManagerClient) {
		this.dataManagerClient = dataManagerClient;
		this.root = null;
	}

	@Override
	public File createNewFolder(final File containingDir) throws IOException {
		final GSFileMetadata fileMetadata =
			dataManagerClient.createDirectory(containingDir.getPath(), "New Folder");
		return new GenomeSpaceFile(fileMetadata, dataManagerClient, getRootDirectory());
	}

	private String getRootDirectory() {
		if (root == null)
			root = dataManagerClient.listDefaultDirectory().getDirectory();
		return root.getPath();
	}

	@Override
	public File getChild(final File parent, final String fileName) {
		throw new IllegalStateException("not implemented!");
	}

	@Override
	public File getDefaultDirectory() {
		if (root == null)
			root = dataManagerClient.listDefaultDirectory().getDirectory();
		return new GenomeSpaceFile(root, dataManagerClient, getRootDirectory());
	}

	@Override
	public File[] getFiles(final File dir, final boolean useFileHiding) {
		return dir.listFiles();
	}

	@Override
	public File getHomeDirectory() {
		return getDefaultDirectory();
	}

	@Override
	public File getParentDirectory(final File dir) {
		return dir == null ? null : dir.getParentFile();
	}

	@Override
	public File[] getRoots() {
		final File[] files = { getDefaultDirectory() };
		return files;
	}

	@Override
	public String getSystemDisplayName(final File f) {
		return f.toString();
	}
	
	@Override
	public String getSystemTypeDescription(File f) {
		//FIXME: here is where we can handle XGMML, SIF etc. nicely!
		return null;
	}

	@Override
	public boolean isFileSystem(final File f) {
		return false;
	}

	@Override
	public boolean isFileSystemRoot(final File dir) {
		if (root == null)
			root = dataManagerClient.listDefaultDirectory().getDirectory();
		final String rootPath = root.getPath() + "/" + root.getName();
		return dir.getPath().equals(rootPath);
	}

	@Override
	public boolean isHiddenFile(final File f) {
		return false;
	}

	@Override
	public boolean isParent(final File folder, final File file) {
		return file.getPath().startsWith(folder.getPath() + "/");
	}

	@Override
	public Boolean isTraversable(final File f) {
		return f.isDirectory();
	}
}
