

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
public class AlignedResults {

	public static void main(String[] args) {
		new AlignedResults(args);	
	}

	List<List<TrackedEvent>> allResults;
	long maxRunTime = Long.MIN_VALUE;
	int maxLevel = Integer.MIN_VALUE;
	Map<String,Color> colorMap;
	public static int width = 800;
	public static int height = 800;
	StringBuffer areaBuffer;
	private static Random rand = new Random(15);
	String[] args;


	public AlignedResults(String[] args) {
		allResults = new LinkedList<List<TrackedEvent>>();
		colorMap = new HashMap<String,Color>();
		this.args = args;

		for ( String fileName : args )
			allResults.add( readResults( fileName ) );

		NeedlemanWunsch nw = new NeedlemanWunsch(allResults.get(0), allResults.get(1));

		createImage(nw.getAligned1(), nw.getAligned2());
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
				maxLevel = Math.max(maxLevel,t.level);
			}
			long diff = localEnd - localBegin;
			maxRunTime = Math.max( maxRunTime, diff );
			maxRunTime *= 1.2;
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			br = null;
		}
		return tel;
	}


    
	private void createImage(List<TrackedEvent> l1, List<TrackedEvent> l2) {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
        paint(g2,l1,l2);
		try {
			ImageIO.write(bi,"png",new File("marge.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void paint(Graphics g, List<TrackedEvent> l1, List<TrackedEvent> l2) {
		areaBuffer = new StringBuffer();

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0,0,width,height);

		int separation = 12;
		int w = separation;
		int h1 = 0;
		int h2 = 0;

		int x1Offset = separation;
		int x2Offset = (separation * (maxLevel + 3));

		int yOffset = 3*separation;
		int totalHeight = height - yOffset;
		int yLoc = yOffset;
		int xLoc = 0;

		g2.setColor(Color.black);
		g2.drawString(args[0], x1Offset, separation);
		g2.drawString(args[1], x2Offset, separation);

		for (int i = 0; i < l1.size(); i++) {

			TrackedEvent t1 = l1.get(i);
			TrackedEvent t2 = l2.get(i);

				System.out.println( "========================================");
			if ( t1 != null ) {
				System.out.println("---- 1     " + t1.toString()); 
				g2.setColor(colorMap.get(t1.signature));

				xLoc = x1Offset + (separation * t1.level); 
				h1 = (int)(((double)(t1.end - t1.begin)/(double)maxRunTime)*(double)totalHeight);
				h1 = h1>2?h1:2; 
				g2.fillRect(xLoc,yLoc,w,h1);
				String sig = t1.signature + ": " + (t1.end - t1.begin);
				addMap(xLoc,yLoc,xLoc+w,yLoc+h1,sig);
			}

			if ( t2 != null ) {
				System.out.println("---- 2     " + t2.toString()); 
				g2.setColor(colorMap.get(t2.signature));

				xLoc = x2Offset + (separation * t2.level); 
				h2 = (int)(((double)(t2.end - t2.begin)/(double)maxRunTime)*(double)totalHeight);
				h2 = h2>2?h2:2; 
				g2.fillRect(xLoc,yLoc,w,h2);
				String sig = t2.signature + ": " + (t2.end - t2.begin);
				addMap(xLoc,yLoc,xLoc+w,yLoc+h2,sig);
			} 

			if ( t1 != null && t2 != null && t1.level == 0 && t2.level == 0 ) {
				yLoc = yLoc + Math.max(h1,h2) + separation;
				System.out.println("t1 and t2: " + yLoc);
			} else if ( t1 != null && t2 == null && t1.level == 0 ) {
				yLoc = yLoc + h1 + separation;
				System.out.println("t1: " + yLoc);
			} else if ( t2 != null && t1 == null && t2.level == 0 ) {
				yLoc = yLoc + h2 + separation;
				System.out.println("t2: " + yLoc);
			}	

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
		html.append("<img src=\"marge.png\" usemap=\"#green\" border=\"0\">\n");
		html.append("<map name=\"green\">\n");

		html.append( areaBuffer.toString() );

		html.append("</map>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		//System.out.println(html.toString());
		try {
			FileWriter fw = new FileWriter("marge.html");
			fw.write(html.toString(),0,html.length());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
