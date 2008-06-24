package cytoscape.plugin.cheminfo;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import cytoscape.task.ui.StringUtils;
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
	
	public static String convertInchiToSmiles(String inchi) {
		String url = "http://www.chemspider.com/inchi.asmx/InChIToSMILES?inchi=" + inchi.trim();
		String smiles = null;
		try {
			String result = URLUtil.download(new URL(url));
			Pattern pattern = Pattern.compile(".*<[^>]*>([^<]*)</string>");
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				smiles = matcher.group(1);
			}
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}

		return smiles;	
	}
	
	public Image depiectInchi(String inchi) {
		return depictWithUCSFSmi2Gif(convertInchiToSmiles(inchi));
	}
}
