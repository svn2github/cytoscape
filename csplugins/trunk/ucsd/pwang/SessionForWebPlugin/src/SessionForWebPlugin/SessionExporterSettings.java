package SessionForWebPlugin;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

public class SessionExporterSettings
{
	public static final byte DESTINATION_DIRECTORY       = 0;
	public static final byte DESTINATION_ZIP_ARCHIVE     = 1;
	public static final byte DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS = 2;
	public static final byte SORT_IMAGES_ALPHABETICALLY  = 0;
	public static final byte SORT_IMAGES_BY_VISUAL_STYLE = 1;
	public static final byte SORT_IMAGES_AS_IS           = 2;
	public static final String FORMAT_PNG                = "png";
	public static final String FORMAT_JPG                = "jpg";
	
	public List<String> networks	    = new ArrayList<String>();
	public byte	destination         = DESTINATION_DIRECTORY;
	public int	numNetworksPerRow   = 3;
	public boolean	doSeparateIntoPages = false;
	public int	numNetworksPerPage  = 20;
	public byte	sortImages          = SORT_IMAGES_ALPHABETICALLY;
	public double	imageZoom           = 1.0;
	public boolean	doSetMaxImageSize   = false;
	public int	maxImageWidth       = 1000;
	public int	maxImageHeight      = 1000;
	public String	imageFormat         = FORMAT_PNG;
	public int	maxThumbnailWidth   = 200;
	public int	maxThumbnailHeight  = 200;
	public File destinationDir = null;
}
