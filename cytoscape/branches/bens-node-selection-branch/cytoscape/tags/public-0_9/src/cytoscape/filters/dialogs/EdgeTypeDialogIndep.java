package cytoscape.filters.dialogs;

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
 * Stand-alone Edge Type dialog.
 * <p>
 * The whole Dialog package is in a temporary state.
 *
 * @author namin@mit.edu
 * @version 2002-02-22
 */
public class EdgeTypeDialogIndep extends JDialog {
    FilterDialogClient client;
    EdgeTypeDialogIndep thisDialog;
    Frame parent;
    Graph2D graph;
    GraphObjAttributes edgeAttributes;
    UndoableGraphHider graphHider;
    String[] interactionTypes;

    EdgeTypeDialog edgeTypeDialog;
    public EdgeTypeDialogIndep (FilterDialogClient client,
			   Frame parent,
			   Graph2D graph,
			   GraphObjAttributes edgeAttributes,
			   UndoableGraphHider graphHider,
			   String[] interactionTypes) {

	super(parent,false);
	setTitle("Edge Type Filter");

	thisDialog = this;
	this.client = client;
	this.parent = parent;
	this. graph = graph;
	this.edgeAttributes = edgeAttributes;
	this.graphHider = graphHider;
	this.interactionTypes = interactionTypes;	

	edgeTypeDialog = new EdgeTypeDialog(edgeAttributes, interactionTypes);

	JPanel actionButtonsPanel = new JPanel();
	actionButtonsPanel.setLayout(new FlowLayout());
	JButton selectButton = new JButton("Select");
	actionButtonsPanel.add(selectButton);
	selectButton.addActionListener(new SelectAction());
	JButton invertButton = new JButton("Invert Selection");
	actionButtonsPanel.add(invertButton);
	invertButton.addActionListener(new InvertAction());
	JButton hideButton = new JButton("Hide Selection");
	actionButtonsPanel.add(hideButton);
	hideButton.addActionListener(new HideAction());
	JButton dismissButton = new JButton("Dismiss");
	actionButtonsPanel.add(dismissButton);
	dismissButton.addActionListener(new DismissAction());

	JPanel buttonsPanel = new JPanel();
	buttonsPanel.setLayout(new BorderLayout());
	buttonsPanel.add(actionButtonsPanel, BorderLayout.EAST);

	JPanel edgeTypePanel = edgeTypeDialog.getPanel();

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	panel.add(edgeTypePanel, BorderLayout.CENTER);
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
	    if (!edgeTypeDialog.setValid()) {
		JOptionPane.showMessageDialog(parent, 
					      "<html>" + edgeTypeDialog.getInvalidMsg() + "</html>");
	    } else {
		EdgeTypeFilter f = (EdgeTypeFilter) edgeTypeDialog.getFilter(graph);
		f.select();
	    }
	    client.redrawGraph();
	}
    }

    public class InvertAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    Edge [] edges = graph.getEdgeArray();
	
	    for (int i=0; i < edges.length; i++) {
		EdgeRealizer edgeRealizer = graph.getRealizer(edges [i]);
		edgeRealizer.setSelected (!edgeRealizer.isSelected());
	    }

	    client.redrawGraph ();
	    
	}
    }

    public class HideAction extends AbstractAction {
	public void actionPerformed (ActionEvent e) {
	    graph.firePreEvent();
	    EdgeCursor edges = graph.selectedEdges(); 
	    while (edges.ok()) {
		graphHider.hide (edges.edge());
		edges.next();
	    }
	    graph.firePostEvent();

	    client.redrawGraph ();
	}
    }
}
