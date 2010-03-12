

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
import cytoscape.performance.track.*;

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
public class AlignedResults implements ImageResults {

	List<List<TrackedEvent>> allResults;
	List<List<TrackedEvent>> alignedResults;
	long maxRunTime = Long.MIN_VALUE;
	int maxLevel = Integer.MIN_VALUE;
	Map<String,Color> colorMap;
	public static int width = 800;
	public static int height = 800;
	StringBuffer areaBuffer;
	String[] args;
	String name;


	public AlignedResults(String name, String... args) {
		allResults = new LinkedList<List<TrackedEvent>>();
		colorMap = new HashMap<String,Color>();
		this.args = args;
		this.name = name;

		for ( String fileName : args )
			allResults.add( readResults( fileName ) );

		MultipleAlign<TrackedEvent> nw = new MultipleAlign<TrackedEvent>(allResults);
		alignedResults = nw.getAlignment();
	}

	public String getExplanation() {
		return "This image presents the results in a normalized fashion where individual " +
		       "events are aligned with one another to facilitate direct comparisons across " +
			   "different versions.";
	}

	public String getMouseOverText() {
		return areaBuffer.toString();
	}

	public String getName() {
		return name; 
	}
    
	public RenderedImage getImage() {
        BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
        paint(g2);
		return bi;
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
					colorMap.put( t.signature, new Color(t.signature.hashCode()) ); 	
				line = br.readLine();
				maxLevel = Math.max(maxLevel,t.level);
			}
			long diff = localEnd - localBegin;
			maxRunTime = Math.max( maxRunTime, diff );
			maxRunTime *= 1.1;
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			br = null;
		}
		return tel;
	}

	private void paint(Graphics g) {
		areaBuffer = new StringBuffer();

		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0,0,width,height);

		int separation = 12;
		int w = separation;
		int h = 0;

		int yOffset = 3*separation;
		int totalHeight = height - yOffset;
		int yLoc = yOffset;
		int xLoc = 0;


		int length = alignedResults.get(0).size();

		// draw the rows first
		for (int j = 0; j < length; j++) {
			int maxY = 0;
			int level = 0;
			for (int i = 0; i < alignedResults.size(); i++) {

				int xOffset = i * (separation * (maxLevel + 3));
	
				if ( j == 0 ) {                    
					g2.setColor(Color.black);
					g2.drawString(args[i], xOffset, separation);
				}

				TrackedEvent t = alignedResults.get(i).get(j);

				if ( t != null ) {
					g2.setColor(colorMap.get(t.signature));
	
					xLoc = xOffset + (separation * t.level); 
					h = (int)(((double)(t.end - t.begin)/(double)maxRunTime)*(double)totalHeight);
					h = h>2?h:2; 
					g2.fillRect(xLoc,yLoc,w,h);
					String sig = t.signature + ": " + (t.end - t.begin);
					addMap(xLoc,yLoc,xLoc+w,yLoc+h,sig);

					maxY = Math.max(maxY,h);
					level = Math.max(t.level,level);
				}
			}
			if ( level == 0 )
				yLoc = yLoc + maxY + separation;
		}
	}

	private void addMap(int x1, int y1, int x2, int y2, String sig) {
		areaBuffer.append("\n");
		areaBuffer.append("<area shape=\"rect\" coords=\"" + x1 + "," + y1 + "," + 
		                  x2  + ","+ y2 + "\" title=\"" + sig + "\">");
						  //onMouseOver=\"writeText('" + sig + 
						  //"')\" onMouseOut=\"writeText('---')\">");

	}
}
