/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: WaitScreen.java,v $
 * $Revision: 1.5 $
 * $Date: 2004/12/21 03:28:14 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;


import java.awt.*;
/* This WaitScreen was originally designed to do the About message, but now
   it's pretty generic. */

public class WaitScreen extends Canvas {
    int line_height;
    int line_widths[];
    int max_width;
    int total_height;
    int margin_width = 10;
    int margin_height = 10;
    
    private String message[];
    private String title;
    private void measure() {
	FontMetrics fm = this.getFontMetrics(this.getFont());
	if (fm == null) return;
	line_height = fm.getHeight();
	max_width = 0;
	for (int i = 0; i < message.length; i++) {
	    line_widths[i] = fm.stringWidth(message[i]);
	    if (line_widths[i] > max_width) {
		max_width = line_widths[i];
	    }
	}
	total_height = message.length * line_height;
    }
    public WaitScreen(String [] m) {
	message = m;
	line_widths = new int[message.length];
    }

    public void addNotify() {super.addNotify(); measure();}

    public Dimension getPreferredSize() {
	return new Dimension(max_width + 2*margin_width,
			     message.length*line_height+2*margin_height);
    }

    public void paint(Graphics g) {
	FontMetrics m = g.getFontMetrics();	
	g.setColor(Color.black);
	int height =   margin_height/ 2 + line_height;
	for (int i = 0; i < message.length; i++) {
	    g.drawString(message[i], 
			 (margin_width + max_width - line_widths[i])/2 , 
			 height);
	    height += line_height;
	}
    }    

}
