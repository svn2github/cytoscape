package cytoscape.filters.dialogs;

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.filters.dialogs.*;
/**
 * Class to create a panel  of many radio buttons affecting a string variabe
 * by implementing all the necessary listeners.
 *
 * A string generalization of BoolPanel.
 *
 * @author namin@mit.edu
 * @author 2002-05-21
 */

public class RadioPanel {
    JPanel radioPanel;
    String var;

    public RadioPanel(String[] labels, String selected) {
	var = selected;
	radioPanel = new JPanel();
	radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	ButtonGroup group = new ButtonGroup();
	RadioListener listener = new RadioListener();
	for (int i = 0; i < labels.length; i++) {
	    String label = labels[i];
	    JRadioButton button = new JRadioButton(label);
	    button.setActionCommand(label);
	    button.setFont(new Font("SansSerif", Font.PLAIN, 11));

	    button.setSelected(selected.equals(label));

	    group.add(button);
	    button.addActionListener(listener);
	    radioPanel.add(button);
	}

    }

    public JPanel getPanel() {
	return radioPanel;
    }

    public String toString() {
	return var;
    }

    class RadioListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    var = e.getActionCommand();
	}
    }

}



