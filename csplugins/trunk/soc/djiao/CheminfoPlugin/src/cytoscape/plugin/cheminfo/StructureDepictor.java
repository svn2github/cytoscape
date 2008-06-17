package cytoscape.plugin.cheminfo;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import cytoscape.util.URLUtil;

public class StructureDepictor {
	
	public StructureDepictor() {
	}
	
	public Image depictWithUCSFSmi2Gif(String smiles) {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?" +
			"smiles=" + smiles;
		Image image = null;
		try {
			InputStream in = URLUtil.getInputStream(new URL(url));
			image = ImageIO.read(in);
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return image;
	}
}
