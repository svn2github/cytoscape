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
 * Class to create a panel 
 * of two radio buttons (one true, one false)
 * affecting a <code>Boolean</code> variabe
 * by implementing all the necessary listeners.
 *
 * @author namin@mit.edu
 * @author 2002-02-22
 */

public class BoolPanel {
    JPanel boolPanel;
    MutableBoolean var;

    public BoolPanel(MutableBoolean var,
		     String trueLabel, String falseLabel) {

	this.var = var;

	JRadioButton trueButton = new JRadioButton(trueLabel);
	trueButton.setActionCommand("true");
	trueButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
	
	JRadioButton falseButton = new JRadioButton(falseLabel);
	falseButton.setActionCommand("false");
	falseButton.setFont(new Font("SansSerif", Font.PLAIN, 11));

	if (var.booleanValue()) {
	    trueButton.setSelected(true);
	} else {
	    falseButton.setSelected(true);
	}

	ButtonGroup group = new ButtonGroup();
	group.add(trueButton);
	group.add(falseButton);

	BoolListener listener = new BoolListener();
	trueButton.addActionListener(listener);
	falseButton.addActionListener(listener);

	boolPanel = new JPanel();
	boolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	boolPanel.add(trueButton);
	boolPanel.add(falseButton);
    }

    public JPanel getPanel() {
	return boolPanel;
    }

    class BoolListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    String status = e.getActionCommand();
	    if (status.equals("false")) {
		var.setValue(false);
	    } else {
		var.setValue(true);
	    }
	}
    }

}



