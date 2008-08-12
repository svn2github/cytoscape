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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


/**
 * A dialog that can display the 2D structure of a molecule
 *
 */
public class MoleculeViewDialog extends JDialog {
    private JLabel label;

    public MoleculeViewDialog(Frame owner) {
        super(owner);
        init();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
 
    public void setSize(int x, int y) {
        super.setSize(x, y);
        int l = (x > y) ? y : x;
        label.setPreferredSize(new Dimension(l - 15, l - 15));
    }
    
    public boolean setDepictor(StructureDepictor depictor) {
    	this.setTitle(depictor.getMoleculeString());
    	Dimension d = label.getPreferredSize();
    	Image image = depictor.depictWithUCSFSmi2Gif((int)d.getWidth(), (int)d.getHeight(), "white");
    	if (null != image) {
        	label.setIcon(new ImageIcon(image));
    	} else {
    		return false;
    	}
        this.getContentPane().add(label, BorderLayout.CENTER);
        return true;
    }

    public void init() {
        label = new JLabel();
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().setBackground(Color.WHITE);
        setResizable(false);
    }
}
