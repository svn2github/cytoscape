package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

/**
 * Storage class for file types that are supported by the plugin for reading and/or writing data.
 * <p>
 * This class contains all instances of
 * {@link de.mpg.mpi_inf.bioinf.netanalyzer.ui.ExtensionFileFilter} created within this plugin.
 * </p>
 * 
 * @author Yassen Assenov
 */
public final class SupportedExtensions {

	/**
	 * Extension filter for JPEG images.
	 */
	public static ExtensionFileFilter jpegFilesFilter = new ExtensionFileFilter(".jpeg", ".jpg",
			"Jpeg images (.jpeg, .jpg)");

	/**
	 * Extension filter for PNG images.
	 */
	public static ExtensionFileFilter pngFilesFilter = new ExtensionFileFilter(".png",
			"Portable Network Graphic images (.png)");

	/**
	 * Extension filter for SVG images.
	 */
	public static ExtensionFileFilter svgFilesFilter = new ExtensionFileFilter(".svg",
			"Scalable Vector Graphics (.svg)");

	/**
	 * Extension filter for .netstats data files.
	 */
	public static final ExtensionFileFilter netStatsFilter = new ExtensionFileFilter(".netstats",
			"Network Statistics (.netstats)");
}
