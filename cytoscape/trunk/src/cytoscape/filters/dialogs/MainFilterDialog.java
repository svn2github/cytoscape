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


import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;

import cytoscape.data.*;
import cytoscape.filters.*;
import cytoscape.filters.dialogs.*;
import cytoscape.*;
/**
 * Main interface to filters.
 * <p>
 * The whole Dialog package is in a temporary state.
 *
 * @author namin@mit.edu
 * @version 2002-02-22
 */
public class MainFilterDialog extends JDialog {
    FilterDialogClient client;
    MainFilterDialog thisDialog;
    Frame parent;
    Graph2D graph;
    GraphObjAttributes nodeAttributes;
    GraphObjAttributes edgeAttributes;
    ExpressionData expressionData;
    UndoableGraphHider graphHider;
    String[] interactionTypes;

    Vector dialogs = new Vector();

    /**
     * Types of dialogs to display.
     */
    int[] types = {
	FilterDialog.TOPOLOGY, 
	FilterDialog.CENTER,
	FilterDialog.INTERACTION, 
	FilterDialog.NODE_TYPE,	
	FilterDialog.EXPRESSION,
	FilterDialog.ATTRIBUTE,
	FilterDialog.COMBINATION
    };

    /**
     * One counter per type.
     * Initialized through private method initCounters().
     */
    int[] counters;

    JTabbedPane tabbedPane;

    public MainFilterDialog (FilterDialogClient client,
			     Frame parent,
			     Graph2D graph,
			     GraphObjAttributes nodeAttributes,
			     GraphObjAttributes edgeAttributes,
			     ExpressionData expressionData,
			     UndoableGraphHider graphHider,
			     String[] interactionTypes) {
	
	super(parent,false);
	setTitle("Filters");

	thisDialog = this;
	this.client = client;
	this.parent = parent;
	this. graph = graph;
	this.expressionData = expressionData;
	this.nodeAttributes = nodeAttributes;
	this.edgeAttributes = edgeAttributes;
	this.graphHider = graphHider;
	this.interactionTypes = interactionTypes;

	initCounters();

	tabbedPane = new JTabbedPane();
	initFilterDialogs();

	JPanel actionButtonsPanel = new JPanel();
	actionButtonsPanel.setLayout(new FlowLayout());
	JButton selectButton = new JButton("Select");
	actionButtonsPanel.add(selectButton);
	selectButton.addActionListener(new SelectAction());
	JButton dismissButton = new JButton("Dismiss");
	actionButtonsPanel.add(dismissButton);
	dismissButton.addActionListener(new DismissAction());

	JPanel filterButtonsPanel = new JPanel();
	filterButtonsPanel.setLayout(new FlowLayout());
	JButton newButton = new JButton("New");
	filterButtonsPanel.add(newButton);

	newButton.addActionListener(new newFilterAction());

	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setLayout(new BorderLayout());
	buttonsPanel.add(filterButtonsPanel, BorderLayout.WEST);
	buttonsPanel.add(actionButtonsPanel, BorderLayout.EAST);

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.add(tabbedPane, BorderLayout.CENTER);
	panel.add(buttonsPanel, BorderLayout.SOUTH);

	setContentPane(panel);
	pack();
	this.setLocation(parent.getLocationOnScreen());
	pack();
	setVisible(true);
    }

    public class DismissAction extends AbstractAction {
	DismissAction () {super ("");}
	public void actionPerformed (ActionEvent e) {
	    thisDialog.dispose ();
	}
    }

    public class SelectAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    int index = tabbedPane.getSelectedIndex();
	    if (index != -1) {
		FilterDialog dialog = (FilterDialog) dialogs.elementAt(index);
		if (!dialog.setValid()) {
		    JOptionPane.showMessageDialog(parent, 
						  "<html>" + dialog.getInvalidMsg() + "</html>");
		} else {
		    Filter f = dialog.getFilter(graph);
		    f.select();
		}
	    }
	    client.redrawGraph();
	}
    }

    public class newFilterAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    int index = tabbedPane.getSelectedIndex();
	    if (index != -1) {
		FilterDialog dialog = (FilterDialog) dialogs.elementAt(index);

		int newIndex = addFilterDialog(dialog.getType());

		if (!dialog.isType(FilterDialog.COMBINATION)) {
		    updateCombinations(newIndex);
		}
		tabbedPane.setSelectedIndex(newIndex);
	    }
	}
    }

    /** initializes counters */
    private void initCounters() {
	int size = types.length;
	counters = new int[size];
	for (int i = 0; i < size; i++) {
	    counters[i] = 0;
	}
    }

    private int getNextIndex(int type) {
	int index = 0;
	for (int i = 0; i < counters.length; i++) {
	    index += counters[i];
	    if (types[i] == type) {
		counters[i]++;
		break;
	    }
	}
	return index;
    }

    private int getCounter(int type) {
	int counter = 0;
	for (int i = 0; i < counters.length; i++) {
	    if (types[i] == type) {
		counter = counters[i];
		break;
	    }
	}
	return counter;
    }

    private int addFilterDialog(int type) {
	FilterDialog dialog;
	if (type == FilterDialog.EXPRESSION) {
	    // assuming expression data is valid
	    dialog = new ExpressionDialog(expressionData, nodeAttributes);
	} else if (type == FilterDialog.COMBINATION) {
	    dialog = new CombinationDialog(dialogs);
	} else if (type == FilterDialog.CENTER) {
	    dialog = new CenterDialog(parent, graph);
	} else if (type == FilterDialog.INTERACTION) {
	    dialog = new InteractionDialog(edgeAttributes, interactionTypes);
	} else if (type == FilterDialog.NODE_TYPE) {
	    dialog = new NodeTypeDialog(nodeAttributes);
	} else if (type == FilterDialog.ATTRIBUTE) {
	    dialog = new AttributeDialog(nodeAttributes);
	} else {
	    // topology by default
	    dialog = new TopologyDialog();
	}
	
	int index = getNextIndex(type);
	dialogs.add(index, dialog);

	JPanel dialogPanel = dialog.getPanel();
	dialogPanel.setName(dialogPanel.getName() + " " + getCounter(type));
	tabbedPane.add(dialogPanel, index);
	return index;
    }
    
    private void initFilterDialogs() {
	for (int i = 0; i < types.length; i++) {
	    if ((expressionData != null || types[i] != FilterDialog.EXPRESSION)) {
		addFilterDialog(types[i]);
	    }
	}
    }

    private void updateCombinations(int newIndex) {
	int combinationCounter = getCounter(FilterDialog.COMBINATION);
	int start = dialogs.size() - combinationCounter;
	for (int i = 0; i < combinationCounter; i++) {
	    CombinationDialog combinationDialog = 
		(CombinationDialog) dialogs.elementAt(i + start);
	    combinationDialog.update(newIndex);
	}
    }
}


