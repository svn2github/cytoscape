

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

import cytoscape.*;
import cytoscape.performance.*;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.imageio.*;


/**
 *
 */
public class HTMLResults {

	public static void main(String[] args) {
		new HTMLResults(args);	
	}

	List<List<TrackedEvent>> allResults;
	long maxRunTime = Long.MIN_VALUE;
	Map<String,Color> colorMap;
	public static int width = 800;
	public static int height = 800;
	StringBuffer areaBuffer;
	private static Random rand = new Random(15);
	String[] args;


	public HTMLResults(String[] args) {
		allResults = new LinkedList<List<TrackedEvent>>();
		colorMap = new HashMap<String,Color>();
		this.args = args;

		for ( String fileName : args )
			allResults.add( readResults( fileName ) );
	
		createImage();
		writeHTML();
	}

	private List<TrackedEvent> readResults(String fileName) {
		List<TrackedEvent> tel = new LinkedList<TrackedEvent>();
		BufferedReader br; 
		try {
			br = new BufferedReader( new FileReader(fileName) );
			long localBegin = Long.MAX_VALUE;
			long localEnd = Long.MIN_VALUE;
			String line = br.readLine();
			while ( line != null ) {
				TrackedEvent t = new TrackedEvent(line);
				tel.add(t);
				localBegin = Math.min( t.begin, localBegin );
				localEnd = Math.max( t.end, localEnd );
				if ( !colorMap.containsKey(	t.signature ) ) 
					colorMap.put( t.signature, new Color(rand.nextInt()) ); 	
				line = br.readLine();
			}
			long diff = localEnd - localBegin;
			maxRunTime = Math.max( maxRunTime, diff );
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			br = null;
		}
		return tel;
	}


    
	private void createImage() {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
        paint(g2);
		try {
			ImageIO.write(bi,"png",new File("homer.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void paint(Graphics g) {
		areaBuffer = new StringBuffer();

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0,0,width,height);

		int separation = 12;
		int w = separation;
		int h = 0;

		int yLoc = 0;
		int xLoc = 0;
		int xOffset = separation;
		int yOffset = 3*separation;
		int totalHeight = height - yOffset;

		//for (List<TrackedEvent> rl : allResults) {
		for (int i = 0; i < allResults.size(); i++) {
			List<TrackedEvent> rl = allResults.get(i);
			String name = args[i];

			long localBegin = Long.MAX_VALUE;
			long localEnd = Long.MIN_VALUE;
			int maxLevel = 0;
			for (TrackedEvent t : rl) {
				localBegin = Math.min( t.begin, localBegin );
				localEnd = Math.max( t.end, localEnd );
				maxLevel = Math.max( maxLevel, t.level );
			}

			g2.setColor(Color.black);
			//g2.drawString(name,xOffset,2*separation);
			// draw the rotated string
			g2.translate(xOffset,2*separation);
			g2.rotate(-Math.PI/16.0);
			g2.drawString(name,0,0);
			g2.rotate(Math.PI/16.0);
			g2.translate(-xOffset,-(2*separation));
						

			for (TrackedEvent t : rl) {
				g2.setColor(colorMap.get(t.signature));

				yLoc = yOffset + (int)(((double)(t.begin - localBegin)/(double)maxRunTime)*(double)totalHeight);
				xLoc = xOffset + (separation * t.level); 
				h = (int)(((double)(t.end - t.begin)/(double)maxRunTime)*(double)totalHeight);
				h = h>2?h:2; // because 1 pixel is too small to mouse over
				g2.fillRect(xLoc,yLoc,w,h);
				addMap(xLoc,yLoc,xLoc+w,yLoc+h,t.signature);
			}

			xOffset += (separation * (maxLevel + 3));
		}
	}

	private void addMap(int x1, int y1, int x2, int y2, String sig) {
		areaBuffer.append("\n");
		areaBuffer.append("<area shape=\"rect\" coords=\"" + x1 + "," + y1 + "," + 
		                  x2  + ","+ y2 + "\" title=\"" + sig + "\">");
						  //onMouseOver=\"writeText('" + sig + 
						  //"')\" onMouseOut=\"writeText('---')\">");

	}
	
	private void writeHTML() {
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
		//html.append("<p id=\"desc\">---</p>\n");
		html.append("<img src=\"homer.png\" usemap=\"#green\" border=\"0\">\n");
		html.append("<map name=\"green\">\n");

		html.append( areaBuffer.toString() );

		html.append("</map>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		//System.out.println(html.toString());
		try {
			FileWriter fw = new FileWriter("homer.html");
			fw.write(html.toString(),0,html.length());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
