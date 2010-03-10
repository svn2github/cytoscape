
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.performance.ui;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

/**
 *
 */
public class HTMLResults {
	
	public static void main(String[] args) {
		ImageResults aligned = new AlignedResults(args);
		ImageResults total = new TotalResults(args);

		writeHTML(total,aligned);
	}

    
	private static String createImage(RenderedImage image, String name) {
		String fileName = name +".png";
		try {
			ImageIO.write(image,"png",new File(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	private static void writeHTML(ImageResults... res) {
		StringBuffer html = new StringBuffer();

		html.append("<html>\n");
		html.append("<head>\n");
		html.append("<script type=\"text/javascript\">\n");
		html.append("function writeText(txt)\n");
		html.append("{\n");
		html.append("document.getElementById(\"desc\").innerHTML=txt\n");
		html.append("}\n");
		html.append("</script>\n");
		html.append("</head>\n");
		html.append("<body>\n");

		for ( ImageResults ir : res ) {
			html.append("<h2>");	
			html.append(ir.getName());	
			html.append(" Result");	
			html.append("</h2>");	
			html.append(ir.getExplanation());	
			html.append("<br>");	
			html.append("<img src=\"");
			html.append(createImage(ir.getImage(),ir.getName()));
			html.append("\" usemap=\"#green\" border=\"0\">\n");
			html.append("<map name=\"green\">\n");
			html.append( ir.getMouseOverText());
			html.append("</map>\n");
		}
		html.append("</body>\n");
		html.append("</html>\n");

		try {
			FileWriter fw = new FileWriter("results.html");
			fw.write(html.toString(),0,html.length());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
