
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
import java.util.Random;

/**
 *
 */
public class HTMLResults {
	
	public static void main(String[] args) {
		ImageResults aligned = new AlignedResults("All Versions Aligned", args);
		ImageResults alignedRecent = new AlignedResults("Most Recent Versions Aligned", 
		                                   args[args.length-2], args[args.length-1]);
		ImageResults total = new TotalResults("All Versions Total Time", args);
		ImageResults totalRecent = new TotalResults("Most Recent Versions Total Time", 
		                                   args[args.length-2], args[args.length-1]);

		writeHTML(total, totalRecent, aligned, alignedRecent);
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
		html.append("<h1>Cytoscape Version Performance Results</h1>");	
		html.append("This document displays images that describe the performance of different versions of Cytoscape.  Each version is instrumented using a time tracking aspect and then executes an indentical set of operations. The time tracking aspect records the duration of specific method calls in Cytoscape.  Each method call is represented by a color block in the image (colors are consistent across the display). The sequence of method calls form a column.  The height of the column indicates the overall time it took to perform the specified set of operations.   Method calls that execute within other method calls are drawn to the right of the parent method call. For instance, the init() method used to start Cytoscape subsumes calls to load plugins, networks, sessions and more, as a consequence the top of the column extends several layers to the right with child method calls.  By hovering your mouse over a colored block, you should be able to see the name of the method call.");	
		html.append("<p/>");
		html.append("The results are broken up into four sections. The first section displays all tested versions to provide perspective our Cytoscape's performance over time.  The image displays the absolute duration of the test for easy comparison across versions.  The second section is the same, except it only includes the two most recent versions.  This allows for closer analysis of the most recent changes made to Cytoscape.  The third section again displays results for all sections, but presents the blocks in a normalized and aligned fashion.  This means that all columns are the same height and that an attempt has been made to align the method blocks across versions. The goal of this display is to allow fine grained comparison of specific method execution times. The fourth section displays aligned results for the two most recent versions. ");
		html.append("<p/>");
		html.append("Actual time values are not displayed as the precise values are highly specific to the given computer and execution environment and are not necessary for comparison across versions.");

		int mapID;
		Random rand = new Random();
		for ( ImageResults ir : res ) {
			html.append("<h2>");	
			html.append(ir.getName());	
			html.append(" Result");	
			html.append("</h2>");	
			html.append(ir.getExplanation());	
			html.append("<br>");	
			html.append("<img src=\"");
			html.append(createImage(ir.getImage(),ir.getName()));
			html.append("\" usemap=\"#");
			mapID = Math.abs(rand.nextInt());
			html.append(mapID);
			html.append("\" border=\"0\">\n");
			html.append("<map name=\"");
			html.append(mapID);
			html.append("\">\n");
			html.append( ir.getMouseOverText());
			html.append("</map>\n");
		}
		html.append("</body>\n");
		html.append("</html>\n");

		try {
			FileWriter fw = new FileWriter("index.html");
			fw.write(html.toString(),0,html.length());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
