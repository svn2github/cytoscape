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
	public File createFileObject(final File dir, final String filename) {
		String dirPath = dir.getPath();
		if (!dirPath.equals("/"))
			dirPath += '/';
		return createFileObject(dirPath + filename);
	}

	@Override
	public File createFileObject(final String path) {
		throw new IllegalStateException("not implemented!");
	}

	@Override
	protected File createFileSystemRoot(final File f) {
		throw new IllegalStateException("not implemented!");
	}

	@Override
	public File createNewFolder(final File containingDir) throws IOException {
System.err.println("+++++++++++++++++++++++ call to GenomeSpaceFileSystemView.createNewFolder("+containingDir+")");
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
System.err.println("************************ GenomeSpaceFileSystemView.getFiles("+dir+") was called!");
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
System.err.println("************************ GenomeSpaceFileSystemView.isFileSystem("+f+") was called!");
		return true;
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
		final String folderPath = folder.getPath();
		boolean retval = file.getPath().startsWith(folderPath.equals("/") ? folderPath : folderPath + "/");
System.err.println("************************ GenomeSpaceFileSystemView.isParent("+folder+", "+file+") returned "+retval);
return retval;
	}

	public boolean isRoot(final File file) {
		return file.getPath().equals("/");
	}

	@Override
	public Boolean isTraversable(final File f) {
System.err.println("************************ GenomeSpaceFileSystemView.isTraversable("+f+") was called!");
		return f.isDirectory();
	}
}
