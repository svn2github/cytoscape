package cytoscape.filters.dialogs;

import y.base.*;
import y.view.*;

import y.algo.GraphHider;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import cytoscape.data.*;
import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;
import cytoscape.*;
/** 
 * Class, which provides an expression dialog
 * within the filter dialog.
 *
 * @author namin@mit.edu
 * @version 2002-02-11
 */
public class InteractionDialog extends FilterDialog {
    public static String DESC = "Select nodes that are in any interaction of the selected types.";
    GraphObjAttributes edgeAttributes;
    JList typeList;
    String[] interactionTypes;
    String search;

    public InteractionDialog(GraphObjAttributes edgeAttributes,
			     String[] interactionTypes) {
	super(FilterDialog.INTERACTION);
	panel.setName("Interaction");
	panel.add(createDescPanel(DESC));
	this.edgeAttributes = edgeAttributes;
	this.interactionTypes = interactionTypes;

	JRadioButton sourceButton = new JRadioButton("source");
	sourceButton.setActionCommand("source");
	sourceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
	
	JRadioButton targetButton = new JRadioButton("target");
	targetButton.setActionCommand("target");
	targetButton.setFont(new Font("SansSerif", Font.PLAIN, 11));

	JRadioButton bothButton = new JRadioButton("both");
	bothButton.setActionCommand("both");
	bothButton.setFont(new Font("SansSerif", Font.PLAIN, 11));

	search = "both";
	bothButton.setSelected(true);

	ButtonGroup searchGroup = new ButtonGroup();
	searchGroup.add(sourceButton);
	searchGroup.add(targetButton);
	searchGroup.add(bothButton);

	SearchListener listener = new SearchListener();
	sourceButton.addActionListener(listener);
	targetButton.addActionListener(listener);
	bothButton.addActionListener(listener);

	JPanel searchPanel = new JPanel();
	searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	searchPanel.add(sourceButton);
	searchPanel.add(targetButton);
	searchPanel.add(bothButton);

	JPanel searchSubPanel = FilterDialog.createSubPanel
	    ("Search in",
	     new JPanel[] {
		 searchPanel
	     });
	panel.add(searchSubPanel);

	typeList = new JList(getTypes());
	JPanel listPanel = new JPanel();
	listPanel.setLayout(new BorderLayout());
	listPanel.add(typeList, BorderLayout.CENTER);
	JPanel subPanel = FilterDialog.createSubPanel
	    ("Interaction Types", 
	     new JPanel[] {
		 listPanel
	     });

	panel.add(subPanel);	
    }

    public boolean setValid() {
	boolean valid = true;
	clearInvalidMsg();
	return valid;
    }

    public Filter getFilter(Graph2D g) {
	InteractionFilter f;
	// used for the side-effect
	setValid();
	Object[] accO = typeList.getSelectedValues();
	String[] accTypes = new String[accO.length];
	for(int i = 0; i < accO.length; i++) {
	    accTypes[i] = (String) accO[i];
	}
	f = new InteractionFilter(g, edgeAttributes,
				  accTypes, search);
	return f;
    }

    public String[] getTypes() {
	return interactionTypes;
    }

    class SearchListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    search = e.getActionCommand();
	}
    }
}
