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
	private String smilesStr;
	private String moleculeString;

	public StructureDepictor(CyNode node) {
		this.node = node;

		String smiles = ChemInfoPlugin.getAttribute(node, "smiles");
		// now search for inchi
		if (null == smiles || "".equals(smiles)) {
			String inchi = ChemInfoPlugin.getAttribute(node, "inchi");
			if (null != inchi && !"".equals(inchi)) {
				this.moleculeString = inchi;
				smiles = convertInchiToSmiles(inchi);
			}
		} else {
			this.moleculeString = smiles;
		}
		this.smilesStr = smiles;
	}

	public String getMoleculeString() {
		return moleculeString;
	}

	public CyNode getNode() {
		return node;
	}

	public void setNode(CyNode node) {
		this.node = node;
	}

	public String getSmiles() {
		return smilesStr;
	}
	
	public String getDepictURL() {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smilesStr;
		return url;
	}	
	
	private String getDepictURL(int width, int height, String bgcolor) {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smilesStr + "&width=" + width + "&height=" + height + "&bgcolor=" + bgcolor;
		return url;
	}

	public Image depictWithUCSFSmi2Gif() {
		if (this.moleculeString == null || "".equals(moleculeString)) {
			return null;
		}
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
		if (this.moleculeString == null || "".equals(moleculeString)) {
			return null;
		}
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
	
	public boolean hasMolecule() {
		return null != moleculeString && !"".equals(moleculeString);
	}
}
