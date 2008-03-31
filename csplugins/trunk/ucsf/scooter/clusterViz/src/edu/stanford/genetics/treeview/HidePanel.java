/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: HidePanel.java,v $
 * $Revision: 1.3 $
 * $Date: 2004/12/21 03:28:13 $
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
import java.awt.event.*;
/**
 * This class simply displays a close button centered in a panel.
 * Clicking the close button hides the window.
 */
class HidePanel extends Panel {
    private final Window m_window;
    public HidePanel(Window window) {
	m_window = window;
	final Button hide_button = new Button("Close");
	hide_button.addActionListener(new ActionListener() {
		// called when close button hit
		public void actionPerformed(ActionEvent evt) {
		    if(evt.getSource() == hide_button) {
			m_window.hide();
		    }
		}
	    });
	add(hide_button);
    }
}
