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
    public static final String REDGREEN_CALC_NAME = "RedGreen";
    public static final String BASIC_CONTINUOUS_CALC_NAME = "BasicContinuous";
    public static final String BASIC_DISCRETE_CALC_NAME = "BasicDiscrete";

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

	VisualMappingManager vizmapper = Cytoscape.getDesktop()
	    .getVizMapManager();

	EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
	eac.setDefaultEdgeColor(new Color(0,0,255));
	eac.setDefaultEdgeFont(new Font(null, Font.PLAIN, 10));
	eac.setDefaultEdgeLabel("");
	eac.setDefaultEdgeLineType(LineType.LINE_1);
	eac.setDefaultEdgeSourceArrow(Arrow.NONE);
	eac.setDefaultEdgeTargetArrow(Arrow.NONE);
	eac.setDefaultEdgeToolTip("");

	NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	nac.setDefaultNodeBorderColor(new Color(0,0,0));
	nac.setDefaultNodeFillColor(new Color(255,153,153));
	nac.setDefaultNodeFont(new Font(null, Font.PLAIN, 12));
	nac.setDefaultNodeHeight(30.0);
	nac.setDefaultNodeLabel("");
	nac.setDefaultNodeLineType(LineType.LINE_1);
	nac.setDefaultNodeShape(ShapeNodeRealizer.ELLIPSE);
	nac.setDefaultNodeToolTip("");
	nac.setDefaultNodeWidth(70.0);

	GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
	gac.setDefaultBackgroundColor(new Color(204, 204, 255));
	gac.setDefaultSloppySelectionColor(new Color(128,128,128));

	

	VisualStyle tutStyle = new VisualStyle("Tutorial", nac, eac, gac);
	vizmapper.setVisualStyle(tutStyle);
		
	// Look for a RedGreen calculator in the catalog
	NodeColorCalculator rgnc = vizmapper.getCalculatorCatalog()
	    .getNodeColorCalculator(REDGREEN_CALC_NAME);
	if (rgnc == null){
	    vizmapper.getCalculatorCatalog()
		.addNodeColorCalculator(redGreenNodeColorCalculator());
	}

	// Look for a BasicDiscrete node shape calculator 
	NodeShapeCalculator bcns = vizmapper.getCalculatorCatalog()
	    .getNodeShapeCalculator(BASIC_CONTINUOUS_CALC_NAME);
	if (bcns == null){
	    vizmapper.getCalculatorCatalog()
		.addNodeShapeCalculator(basicContinuousNodeShapeCalculator());
	}

	// Look for a BasicDiscrete node shape calculator 
	EdgeColorCalculator bdec = vizmapper.getCalculatorCatalog()
	    .getEdgeColorCalculator(BASIC_CONTINUOUS_CALC_NAME);
	if (bdec == null){
	    vizmapper.getCalculatorCatalog()
		.addEdgeColorCalculator(basicDiscreteEdgeColorCalculator());
	}
    }// prepareVizmapper


    /**
     * Creates the <code>NodeColorCalculator</code> that will be used to
     * calculate the fill color of nodes.
     */
    protected NodeColorCalculator redGreenNodeColorCalculator() {
	
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
	
	NodeColorCalculator redGreenColorCalculator 
	    = new GenericNodeColorCalculator(REDGREEN_CALC_NAME, contMapping);
	return redGreenColorCalculator;
    }// createCalculator



    /**
     * Creates the <code>NodeColorCalculator</code> that will be used to
     * calculate the fill color of nodes.
     */
    protected NodeShapeCalculator basicContinuousNodeShapeCalculator() {
	
	ContinuousMapping contMapping = new ContinuousMapping(new Byte(ShapeNodeRealizer.ELLIPSE), ObjectMapping.NODE_MAPPING);

	BoundaryRangeValues brVals;
	brVals = new BoundaryRangeValues();
	// blue below, red equal, red greater 
	brVals.lesserValue = new Byte(ShapeNodeRealizer.ELLIPSE);
	brVals.equalValue = new Byte(ShapeNodeRealizer.ELLIPSE);
	brVals.greaterValue = new Byte(ShapeNodeRealizer.ELLIPSE);
	contMapping.addPoint(-1.0, brVals);
	
	// white
	brVals = new BoundaryRangeValues();
	brVals.lesserValue = new Byte(ShapeNodeRealizer.ELLIPSE);
	brVals.equalValue = new Byte(ShapeNodeRealizer.DIAMOND);
	brVals.greaterValue = new Byte(ShapeNodeRealizer.RECT);
	contMapping.addPoint(0.0, brVals);

	// red
	brVals = new BoundaryRangeValues();
	brVals.lesserValue = new Byte(ShapeNodeRealizer.RECT);
	brVals.equalValue = new Byte(ShapeNodeRealizer.RECT);
	brVals.greaterValue = new Byte(ShapeNodeRealizer.RECT);
	contMapping.addPoint(1.0, brVals);
	
	NodeShapeCalculator basicContinuous 
	    = new GenericNodeShapeCalculator(BASIC_CONTINUOUS_CALC_NAME, 
					     contMapping);
	return basicContinuous;
    }// createCalculator


    /**
     * Creates the <code>NodeColorCalculator</code> that will be used to
     * calculate the fill color of nodes.
     */
    protected EdgeColorCalculator basicDiscreteEdgeColorCalculator() {
	
	DiscreteMapping discMapping = new DiscreteMapping(Color.BLUE, "EDGE_COLOR", ObjectMapping.EDGE_MAPPING);
	discMapping.putMapValue("cr", new Color(255,150,0));
	discMapping.putMapValue("gd", new Color(0,255,255));
	discMapping.putMapValue("gl", new Color(0,255,0));
	discMapping.putMapValue("pd", new Color(255,255,0));
	discMapping.putMapValue("pp", new Color(0,0,255));
	discMapping.putMapValue("pr", new Color(255,0,255));
	discMapping.putMapValue("rc", new Color(150,150,0));
	
	EdgeColorCalculator basicDiscrete 
	    = new GenericEdgeColorCalculator(BASIC_DISCRETE_CALC_NAME, 
					     discMapping);
	return basicDiscrete;
    }// createCalculator




    public void setVisualStyle () {
	VisualMappingManager vizmapper = Cytoscape.getDesktop()
	    .getVizMapManager();
	vizmapper.setVisualStyle("Tutorial");
	System.out.println("Activated");
    }


}// VizmapperInit
