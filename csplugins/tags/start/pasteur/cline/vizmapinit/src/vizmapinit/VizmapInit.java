/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A class that performs basic runtime initializiation of the vizmapper,
 * should there not be a vizmapper present.
 * 
 * @author Melissa Cline, cline@pasteur.fr
 * @version %I%, %G%
 * @since 2.3
 */

// TODO: LEFT HERE Handle meta-nodes!!!!
package VizmapInit;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.io.*;

import cytoscape.*;
import cytoscape.visual.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.data.*;

import cern.colt.list.*;
import giny.model.*;

public class VizmapInit extends AbstractAction {


   /**
     * The name of the node color calculator to be created here.
     */
    public static final String NODE_COLOR_CALC_NAME = "RedGreen";

    protected NodeColorCalculator redGreenColorCalculator;

    /**
      * Constructor.
      */
    public VizmapInit() {
	super("VizmapInit...");
    }

    /**
     * Constructor.
     * 
     * @param parent_menu
     *       the JMenu to which this instance will be added if null, the
     *       instance won't be added anywhere
     */
    public VizmapInit(JMenu parent_menu) {
	this();
	prepareVizmapper();
    }// VizmapInit

    public void actionPerformed(ActionEvent event) {
    }

    /**
     * Get ready for playing the movie.
     */
    public void prepareVizmapper() {
	CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();

	VisualMappingManager manager = Cytoscape.getDesktop()
	    .getVizMapManager();
		
	// See if there is already a RedGreen calculator in the catalog
	NodeColorCalculator check = manager.getCalculatorCatalog()
	    .getNodeColorCalculator(NODE_COLOR_CALC_NAME);
	
	if (check != null){
	    this.redGreenColorCalculator = check;
	} else if (this.redGreenColorCalculator == null) {
	    this.redGreenColorCalculator = createCalculator();
	}

	// Doublecheck that the catalog has a copy of this calculator
	NodeColorCalculator check2 = manager.getCalculatorCatalog()
	    .getNodeColorCalculator(NODE_COLOR_CALC_NAME);
	if (check2 == null) {
	    manager.getCalculatorCatalog().addNodeColorCalculator(
				       this.redGreenColorCalculator);
	}
    }// prepareVizmapper


    /**
     * Creates the <code>NodeColorCalculator</code> that will be used to
     * calculate the fill color of nodes.
     */
    protected NodeColorCalculator createCalculator() {
	
	ContinuousMapping contMapping = new ContinuousMapping(new Color(204,
				   204, 204), ObjectMapping.NODE_MAPPING);

	BoundaryRangeValues brVals;
	brVals = new BoundaryRangeValues();
	// blue below, red equal, red greater 
	brVals.lesserValue = new Color(0, 0, 255);
	brVals.equalValue = new Color(255, 0, 0);
	brVals.greaterValue = new Color(255, 0, 0);
	contMapping.addPoint(-2.5, brVals);
	
	// white
	brVals = new BoundaryRangeValues();
	brVals.lesserValue = new Color(255, 255, 255);
	brVals.equalValue = new Color(255, 255, 255);
	brVals.greaterValue = new Color(255, 255, 255);
	contMapping.addPoint(0.0, brVals);

	// red
	brVals = new BoundaryRangeValues();
	brVals.lesserValue = new Color(0, 255, 0);
	brVals.equalValue = new Color(0, 255, 0);
	brVals.greaterValue = new Color(0, 0, 0);
	contMapping.addPoint(2.1, brVals);
	
	this.redGreenColorCalculator = new GenericNodeColorCalculator(
			      NODE_COLOR_CALC_NAME, contMapping);
	return this.redGreenColorCalculator;
    }// createCalculator


}// VizmapperInit
