/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.plugin.cheminfo.structure;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import cytoscape.CyNode;
import cytoscape.plugin.cheminfo.ChemInfoPlugin;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;
import cytoscape.util.URLUtil;

public class StructureDepictor implements Comparable<StructureDepictor> {
	private CyNode node;
	private String smilesStr;
	private String moleculeString;
	private String attribute;
	private AttriType attrType;

	public StructureDepictor(CyNode node, String attribute, AttriType attrType) {
		this.node = node;
		this.attribute = attribute;
		this.attrType = attrType;

		this.moleculeString = ChemInfoPlugin.getAttribute(node, attribute);
		if (attrType == AttriType.smiles) {
			this.smilesStr = moleculeString;
		} else if (attrType == AttriType.inchi) {
			this.smilesStr = ChemInfoPlugin.convertInchiToSmiles(moleculeString);
		}
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
			image = ImageIO.read(in);
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return image;
	}	

	
	public boolean hasMolecule() {
		return null != moleculeString && !"".equals(moleculeString);
	}

	public int compareTo(StructureDepictor o) {
		return this.getSmiles().compareTo(o.getSmiles());
	}
}
