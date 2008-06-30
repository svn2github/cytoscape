package cytoscape.plugin.cheminfo;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import cytoscape.CyNode;
import cytoscape.util.URLUtil;

public class StructureDepictor {
	private CyNode node;
	private String smiles;
	private String nodeText;
	private Image image;

	public StructureDepictor(CyNode node) {
		this.node = node;

		String smiles = ChemInfoPlugin.getAttribute(node, "smiles");
		// now search for inchi
		if (null == smiles || "".equals(smiles)) {
			String inchi = ChemInfoPlugin.getAttribute(node, "inchi");
			this.nodeText = inchi;
			smiles = convertInchiToSmiles(inchi);
		} else {
			this.nodeText = smiles;
		}
		this.smiles = smiles;
		//this.image = depictWithUCSFSmi2Gif(smiles);
	}

	public String getNodeText() {
		return nodeText;
	}

	public CyNode getNode() {
		return node;
	}

	public void setNode(CyNode node) {
		this.node = node;
	}

	public String getSmiles() {
		return smiles;
	}
	
	public String getDepictURL() {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smiles;
		return url;
	}	
	
	public String getDepictURL(int width, int height, String bgcolor) {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smiles + "&width=" + width + "&height=" + height + "&bgcolor=" + bgcolor;
		return url;
	}

	public Image depictWithUCSFSmi2Gif() {
		String url = getDepictURL();
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
	
	public Image depictWithUCSFSmi2Gif(int width, int height, String bgcolor) {
		String url = getDepictURL(width, height, bgcolor);
		Image image = null;
		try {
			InputStream in = URLUtil.getInputStream(new URL(url));
			System.out.println(url);
			image = ImageIO.read(in);
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return image;
	}	

	public static String convertInchiToSmiles(String inchi) {
		String url = "http://www.chemspider.com/inchi.asmx/InChIToSMILES?inchi="
				+ inchi.trim();
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
		this.smiles = convertInchiToSmiles(inchi);
		return depictWithUCSFSmi2Gif();
	}
}
