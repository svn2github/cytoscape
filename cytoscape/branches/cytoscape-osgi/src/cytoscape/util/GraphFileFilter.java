package cytoscape.util;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

import cytoscape.data.readers.GraphReader;

public interface GraphFileFilter {

	/**
	 * Returns true if this class is capable of processing the specified file.
	 *
	 * @param f File
	 */
	public abstract boolean accept(File f);

	/**
	 * Returns true if this class is capable of processing the specified file.
	 *
	 * @param dir       Directory.
	 * @param fileName  File name.
	 *
	 */
	public abstract boolean accept(File dir, String fileName);

	/**
	 * Returns true if this class is capable of processing the specified file.
	 *
	 * @param fileName  File name.
	 */
	public abstract boolean accept(String fileName);

	/**
	 * Returns true if this class is capable of processing the specified URL
	 *
	 * @param url the URL
	 * @param contentType the content-type of the URL
	 *
	 */
	public abstract boolean accept(URL url, String contentType);

	/**
	 * Return the extension portion of the file's name.
	 *
	 * @see #getExtension
	 * @see FileFilter#accept
	 */
	public abstract String getExtension(File f);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param filename DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract String getExtension(String filename);

	/**
	 * Adds a filetype "dot" extension to filter against.
	 * <p/>
	 * For example: the following code will create a filter that filters
	 * out all files except those that end in ".jpg" and ".tif":
	 * <p/>
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addExtension("jpg");
	 * filter.addExtension("tif");
	 * <p/>
	 * Note that the "." before the extension is not needed and will be ignored.
	 */
	public abstract void addExtension(String extension);

	/**
	 * Adds a content-type to filter against.
	 * <p/>
	 * For example: the following code will create a filter that filters
	 * out all streams except those that are of type "text/xgmml+xml" 
	 * and "text/xgmml":
	 * <p/>
	 * ExampleFileFilter filter = new ExampleFileFilter();
	 * filter.addContentType("text/xgmml+xml");
	 * filter.addContentType("text/xgmml");
	 * <p/>
	 */
	public abstract void addContentType(String type);

	/**
	 * Returns the human readable description of this filter. For
	 * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 * @see FileFilter#getDescription
	 */
	public abstract String getDescription();

	/**
	 * Sets the human readable description of this filter. For
	 * example: filter.setDescription("Gif and JPG Images");
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 */
	public abstract void setDescription(String description);

	/**
	 * Determines whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 * <p/>
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see isExtensionListInDescription
	 */
	public abstract void setExtensionListInDescription(boolean b);

	/**
	 * Returns whether the extension list (.jpg, .gif, etc) should
	 * show up in the human readable description.
	 * <p/>
	 * Only relevent if a description was provided in the constructor
	 * or using setDescription();
	 *
	 * @see getDescription
	 * @see setDescription
	 * @see setExtensionListInDescription
	 */
	public abstract boolean isExtensionListInDescription();

	/**
	 * Returns the Set of file extension names.
	 */
	public abstract Set getExtensionSet();

	/**
	 * Returns the reader.  This should be overridden by file type subclasses.
	 */
	public abstract GraphReader getReader(String fileName);

	/**
	 * Returns the reader.  This should be overridden by file type subclasses.
	 */
	public abstract GraphReader getReader(URL url, URLConnection conn);

	/**
	 * Returns the nature of the file.  "Nature" refers to a grouping
	 * of file types.  For instance, GML, XGMML, and SIF are all file formats
	 * that contain graphs, therefore they belong to the GRAPH_NATURE.  This
	 * allows the ImportHandler to return all file types with the same nature.
	 */
	public abstract String getFileNature();

	/**
	 * Sets the nature of the files for this filter.
	 * The files can be of the nature: Node, Edge, Graph, or Vizmap;
	 *
	 * @see setDescription
	 * @see setExtensionListInDescription
	 * @see isExtensionListInDescription
	 */
	public abstract void setFileNature(String nature);

}