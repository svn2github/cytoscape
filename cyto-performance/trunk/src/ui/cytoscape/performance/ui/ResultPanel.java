
package cytoscape.performance.ui;

import cytoscape.*;
import cytoscape.performance.*;
import cytoscape.performance.track.*;
import java.util.*;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import java.io.*;

public class ResultPanel extends JPanel {

	List<TrackedEvent> results;
	long duration = 0;
	Map<String,Color> colorMap;
	public static int width = 800;
	public static int height = 800;
	long globalBegin = Long.MAX_VALUE;
	long globalEnd = Long.MIN_VALUE;
	StringBuffer areaBuffer;

	private static Random rand = new Random(15);
	private static final long serialVersionUID = 1850355553690996166L;

    public ResultPanel(List<TrackedEvent> l) {
		super();
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.white);
		results = l;
		colorMap = new HashMap<String,Color>();
		for (TrackedEvent t : l) {
			globalBegin = Math.min( t.begin, globalBegin );
			globalEnd = Math.max( t.end, globalEnd );
			if ( !colorMap.containsKey(	t.signature ) ) 
				colorMap.put( t.signature, new Color(rand.nextInt())); 	
		}

		areaBuffer = new StringBuffer();

		duration = globalEnd - globalBegin;
    }


	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.white);
		g2.fillRect(0,0,width,height);

		int separation = 12;
		int w = separation;
		int h = 0;
		int totalHeight = height - (2*separation);

		if ( results != null && results.size() > 0 ) {
			for (TrackedEvent t : results) {
				g2.setColor(colorMap.get(t.signature));

				int yLoc = 2*separation + ((int)((double)((t.begin - globalBegin)*totalHeight)/
				                          (double)(globalEnd - globalBegin)));
				int xLoc = separation + (separation * 2 * t.level);
				h = (int)(((double)(t.end - t.begin)/(double)duration)*(double)totalHeight);
				g2.fillRect(xLoc,yLoc,w,h>2?h:2);
				int nx = xLoc + w;
				int ny = yLoc + h;
				addMap(xLoc,yLoc,xLoc+w,yLoc+h,t.signature);
				//g2.drawString(t.signature,xLoc + separation, yLoc);
			}

		} 

		writeHTML();
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
		html.append("<img src=\"total-time.png\" usemap=\"#green\" border=\"0\">\n");
		html.append("<map name=\"green\">\n");

		html.append( areaBuffer.toString() );

		html.append("</map>\n");
		html.append("</body>\n");
		html.append("</html>\n");

		//System.out.println(html.toString());
		try {
			FileWriter fw = new FileWriter("total-time.html");
			fw.write(html.toString(),0,html.length());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
