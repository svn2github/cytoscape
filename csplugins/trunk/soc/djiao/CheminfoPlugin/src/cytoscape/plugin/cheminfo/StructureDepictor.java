package cytoscape.plugin.cheminfo;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class StructureDepictor {
	
	public StructureDepictor() {
	}
	
	public Image depictWithUCSFSmi2Gif(String smiles) {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?" +
			"smiles=" + smiles;
		Image image = null;
		try {
			image = ImageIO.read(new URL(url));
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return image;
	}
}
