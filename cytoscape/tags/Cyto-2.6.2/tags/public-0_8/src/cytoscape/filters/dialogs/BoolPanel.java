package cytoscape.filters.dialogs;

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

