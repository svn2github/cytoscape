package cytoscape.filters.dialogs;

import y.base.*;
import y.view.*;

import cytoscape.undo.UndoableGraphHider;

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
public class ExpressionDialog extends FilterDialog {
    public static String DESC = "Select all afffected nodes as well as unaffected nodes which have the indicated number of affected neighbors within the indicated depth.";
    ExpressionData expressionData;
    GraphObjAttributes nodeAttributes;

    double cutoff;
    JTextField cutoffField;
   
    int nConds;
    JTextField nCondsField;

    int nNeighbors;
    JTextField nNeighborsField;

    int maxDepth;
    JTextField maxDepthField;

    // Boolean objects, so that their value
    // can be changed by reference.
    MutableBoolean cutoffLess = new MutableBoolean(false);
    MutableBoolean hideSingletons = new MutableBoolean(false);
    MutableBoolean useRatio = new MutableBoolean(false);

    public ExpressionDialog(ExpressionData expressionData,
			    GraphObjAttributes nodeAttributes) {
	super(FilterDialog.EXPRESSION);
	panel.setName("Expression");
	panel.add(createDescPanel(DESC));
	this.expressionData = expressionData;
	this.nodeAttributes = nodeAttributes;

	JPanel cutoffLessPanel = FilterDialog.createFieldPanel
	    ("Cutoff Value", 
	     new BoolPanel
		 (cutoffLess, 
		  "smaller than", "greater than").getPanel());

	cutoffField = new JTextField(4);
	JPanel cutoffPanel = FilterDialog.createFieldPanel
	    (cutoffField);

	JPanel useRatioPanel = FilterDialog.createFieldPanel
	    ("Compare Value With", 
	     new BoolPanel
		 (useRatio, 
		  "Ratio", "Significance").getPanel());

	nCondsField = new JTextField(4);
	JPanel nCondsPanel = FilterDialog.createFieldPanel
	    ("Minimum Number of Meeting Conditions",
	     nCondsField);
	
	JPanel defSubPanel = FilterDialog.createSubPanel
	    ("Definition of Affected Nodes", 
	     new JPanel[] {
		 cutoffLessPanel,
		 cutoffPanel,
		 useRatioPanel,
		 nCondsPanel
	     });

	panel.add(defSubPanel);


	nNeighborsField = new JTextField(4);
	JPanel nNeighborsPanel = FilterDialog.createFieldPanel
	    ("Minimum Number of Affected Neighbors", 
	     nNeighborsField);

	maxDepthField = new JTextField(4);
	JPanel maxDepthPanel = FilterDialog.createFieldPanel
	    ("Within Depth",
	     maxDepthField);

	JPanel hideSingletonsPanel = FilterDialog.createFieldPanel
	    ("Hide Singletons?", 
	     new BoolPanel
		 (hideSingletons, 
		  "Yes", "No").getPanel());


	JPanel reqPanel = FilterDialog.createSubPanel
	    ("Requirements for Unaffected Nodes", 
	     new JPanel[] {
		 nNeighborsPanel, 
		 maxDepthPanel
	     });


	JPanel optionsPanel = FilterDialog.createSubPanel
	    ("Options",
	     new JPanel[] {
		 hideSingletonsPanel
	     });

	panel.add(reqPanel);
	panel.add(optionsPanel);

    }

    public boolean setValid() {
	boolean valid = true;
	String posIntVerbalGroup = "should be a positive integer";
	
	clearInvalidMsg();

	cutoff = FilterDialog.parseDouble(cutoffField.getText());
	if (cutoff == -1) {
	    valid = false;
	    // default
	    cutoff = 1;
	    addInvalidMsg("Cutoff value", "should be a number");
	}
	
	nConds = FilterDialog.parsePosInt(nCondsField.getText());
	if (nConds == -1) {
	    valid = false;
	    // default
	    nConds = 1;
	    addInvalidMsg("Number of Conditions", posIntVerbalGroup);
	}

	nNeighbors = FilterDialog.parsePosInt(nNeighborsField.getText());
	if (nNeighbors == -1) {
	    valid = false;
	    // default
	    nNeighbors = 1;
	    addInvalidMsg("Number of Neighbors", posIntVerbalGroup);
	}

	maxDepth = FilterDialog.parsePosInt(maxDepthField.getText());
	if (maxDepth == -1) {
	    valid = false;
	    // default
	    maxDepth = 1;
	    addInvalidMsg("Depth", posIntVerbalGroup);
	}

	return valid;
    }

    public Filter getFilter(Graph2D g) {
	ExpressionFilter f;
	// used for the side-effect
	setValid();
	f = new ExpressionFilter(g, expressionData, nodeAttributes,
				 cutoff, cutoffLess.booleanValue(), nConds,
				 useRatio.booleanValue(),
				 nNeighbors, maxDepth,
				 hideSingletons.booleanValue());
	return f;
    }
}
