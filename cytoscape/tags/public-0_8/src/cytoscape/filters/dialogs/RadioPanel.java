package cytoscape.filters.dialogs;

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

