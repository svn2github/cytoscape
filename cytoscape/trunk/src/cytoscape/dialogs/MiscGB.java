// MiscGB.java:  miscellaneous static GridBagLayout utilities
//--------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.dialogs;
//--------------------------------------------------------------------------------------
import java.io.*;
import java.util.*;

import javax.swing.*;
import java.awt.*;

import cytoscape.util.Misc;
import cytoscape.util.MutableColor;
import cytoscape.dialogs.GridBagGroup;

//------------------------------------------------------------------------------
public class MiscGB {

    // sets GridBagConstraints.
    public static void pad(GridBagConstraints c, int padx, int pady) {
	c.ipadx = padx;	c.ipady = pady;
    }
    public static void set(GridBagConstraints c,
			   int x, int y, int w, int h, int f) {
	c.gridx = x;	c.gridy = y;
	c.gridwidth = w;	c.gridheight = h;
	c.fill = f;
    }
    public static void set(GridBagConstraints c,
			   int x, int y, int w, int h) {
	set(c,x,y,w,h,GridBagConstraints.NONE);
    }
    public static void set(GridBagConstraints c,
			   int x, int y) {
	set(c,x,y,1,1,GridBagConstraints.NONE);
    }

    // inserts a component into a panel with a GridBagLayout.
    public static void insert (JPanel panel,
			       Component comp,
			       GridBagLayout bag,
			       GridBagConstraints c) {
	if(bag==null) System.out.println("bag is null");
	if(comp==null) System.out.println("comp is null");
	if(c==null) System.out.println("c is null");
	if(panel==null) System.out.println("panel is null");
	bag.setConstraints(comp,c);
	panel.add(comp);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y) {
	set(gbg.constraints, x, y);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y, int w, int h) {
	set(gbg.constraints, x, y, w, h);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static void insert (GridBagGroup gbg,
			       Component comp,
			       int x, int y, int w, int h, int f) {
	set(gbg.constraints, x, y, w, h, f);
	insert(gbg.panel, comp, gbg.gridbag, gbg.constraints);
    }

    public static JLabel createColorLabel(Color c) {
	JLabel label = new JLabel("    ");
	label.setOpaque(true);
	label.setBackground(c);
	return label;
    }

    public static JButton buttonAndColor(JDialog parent, MutableColor mc,
					 JLabel l, String bTitle) {
	JButton jb = new JButton(bTitle);
	jb.addActionListener
	    (new GeneralColorDialogListener
		(parent,mc,l,"Choose a " + bTitle));
	return jb;
    }

}

