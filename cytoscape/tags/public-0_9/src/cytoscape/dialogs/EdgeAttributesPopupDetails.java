//
// EdgeAttributesPopupDetails.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.dialogs;

import cytoscape.*;
import javax.swing.*;
import y.base.*;
import y.view.*;
import java.awt.*;
import java.awt.event.*;


/**
 * This class provides a detailed list of attribute information for a
 * given edge.
 */
public class EdgeAttributesPopupDetails extends JDialog {

    public EdgeAttributesPopupDetails (Frame parentFrame, String name,
				       GraphObjAttributes edgeAttributes) {
	super (parentFrame, "Edge Attributes - "+name, false);

	JScrollPane scrollPanel = new JScrollPane(getContentComponent(edgeAttributes, name));

	JPanel buttonPanel = new JPanel();
	JButton dismissButton = new JButton ("Dismiss");
	dismissButton.addActionListener (new DismissAction (this));
	buttonPanel.add(dismissButton, BorderLayout.CENTER);
	
	JPanel panel = new JPanel();
	panel.setLayout (new BorderLayout());
	panel.add(scrollPanel, BorderLayout.CENTER);
	panel.add(buttonPanel, BorderLayout.SOUTH);

	setContentPane(panel);
    }

    protected Component getContentComponent
	(GraphObjAttributes edgeAttributes, String name) {
	
	String contents = name;

	if (name.length() == 0) {
	    contents = "Unable to locate attributes for selected edge";
	} else {
	    String attributes[] = edgeAttributes.getAttributeNames();
	    for (int i = 0; i < attributes.length; i++) {
		Object value = edgeAttributes.getValue(attributes[i], name);

		if (value != null)
		    contents += "\n\n" + attributes[i] + ":\n" + value;
	    }
	}

	JTextArea textArea = new JTextArea(contents, 8, 40);

	return textArea;
    }

    protected class DismissAction extends AbstractAction {
	private JDialog dialog;

	DismissAction (JDialog dialog) { super(""); this.dialog = dialog; }

	public void actionPerformed (ActionEvent e) {
	    dialog.dispose();
	}
    }
}
