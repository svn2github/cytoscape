package BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere
 * * Date: Mar.25.2005
 * * Description: Class that defines the BiNGO visual style used in the Cytoscape visualization of BiNGO results.     
 **/


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import java.awt.*;

/**
 * *****************************************************************
 * HelpMenuBar.java     Steven Maere (c) March 2005
 * ----------------
 * <p/>
 * Class that defines the BiNGO visual style used in the Cytoscape visualization of BiNGO results.
 * ******************************************************************
 */

public class TheVisualStyle {

/*-----------------FIELDS----------------------*/
    private String networkName;
    private double alpha;
    private static String BINGO_VS;
    private static String NODE_TYPE;
    private static String NODE_COLOR;
    private static String NODE_LABEL;
    private static String NODE_SIZE;
    private static String EDGE_TYPE;

/*-----------------CONSTRUCTOR----------------------*/

    public TheVisualStyle(String networkName, double alpha) {
        this.networkName = networkName;
        this.alpha = alpha;
        this.BINGO_VS = "BiNGO_" + networkName;
        this.NODE_TYPE = "nodeType_" + networkName;
        this.NODE_COLOR = "nodeFillColor_" + networkName;
        this.NODE_LABEL = "description_" + networkName;
        this.NODE_SIZE = "nodeSize_" + networkName;
        this.EDGE_TYPE = "edgeType_" + networkName;
    }

/*-------------------METHODS-----------------------*/
    
    public void adaptVisualStyle(VisualStyle style, CyNetwork network) {

        //---------------------set defaults--------------------------------------//

        style.getNodeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
        style.getNodeAppearanceCalculator().getDefaultAppearance().setNodeSizeLocked(true); 
        style.getNodeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE,24);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_SHAPE,ArrowShape.DELTA);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_TGTARROW_COLOR,Color.black);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_SRCARROW_SHAPE,ArrowShape.NONE);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_STYLE,LineStyle.SOLID);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_LINE_WIDTH,2.0f);
        style.getEdgeAppearanceCalculator().getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR,Color.black);
        style.getGlobalAppearanceCalculator().setDefaultBackgroundColor(Color.white);

        // ------------------------------ Set the label ------------------------------//

        // Display NODE_LABEL as a label
        PassThroughMapping m = new PassThroughMapping(new String(), ObjectMapping.NODE_MAPPING);
        m.setControllingAttributeName(NODE_LABEL, network, false);
        Calculator nlc = new BasicCalculator("Node Description_" + networkName, m,VisualPropertyType.NODE_LABEL);
        style.getNodeAppearanceCalculator().setCalculator(nlc);

        //---------------------------Continuous node color--------------------------//

        ContinuousMapping colorMapping = new ContinuousMapping(new Color(255, 0, 0), ObjectMapping.NODE_MAPPING);
        colorMapping.setControllingAttributeName(NODE_COLOR, network, false);

        // The following code defines the range of values

        BoundaryRangeValues colbrVal1 = new BoundaryRangeValues();
        Color nada = new Color(255, 255, 255);
        Color colmin = new Color(255, 255, 0);
        double cols = -(Math.log(alpha) / Math.log(10));
        colbrVal1.lesserValue = nada;
        colbrVal1.equalValue = colmin;
        colbrVal1.greaterValue = colmin;
        colorMapping.addPoint(cols, colbrVal1);

        BoundaryRangeValues colbrVal2 = new BoundaryRangeValues();
        Color colmax = new Color(255, 127, 0);
        cols = - (Math.log(alpha) / Math.log(10)) + 5.0;
        colbrVal2.lesserValue = colmax;
        colbrVal2.equalValue = colmax;
        colbrVal2.greaterValue = colmax;
        colorMapping.addPoint(cols, colbrVal2);


        Calculator colorCalculator =
                new BasicCalculator("Bingo Node Color_" + networkName, colorMapping,VisualPropertyType.NODE_FILL_COLOR);
        style.getNodeAppearanceCalculator().setCalculator(colorCalculator);

        //--------------------------- Size of the nodes ----------------------------//


        ContinuousMapping wMapping = new ContinuousMapping(new Double(50), ObjectMapping.NODE_MAPPING);
        wMapping.setControllingAttributeName(NODE_SIZE, network, false);
        ContinuousMapping hMapping = new ContinuousMapping(new Double(50), ObjectMapping.NODE_MAPPING);
        hMapping.setControllingAttributeName(NODE_SIZE, network, false);

        // The following code defines the range of values

        BoundaryRangeValues brVals;
        int j;
        for (j = 0; j <= 1; j++) {
            brVals = new BoundaryRangeValues();
            Double size = new Double(380 * j + 20);
            double s = 99 * j + 1;
            brVals.lesserValue = size;
            brVals.equalValue = size;
            brVals.greaterValue = size;
            wMapping.addPoint(s, brVals);
            hMapping.addPoint(s, brVals);
        }

        Calculator nodeSizeCalculator = new BasicCalculator("Bingo Node Size_" + networkName, wMapping, VisualPropertyType.NODE_SIZE);
        style.getNodeAppearanceCalculator().setCalculator(nodeSizeCalculator);
    }
    
    
    public VisualStyle createVisualStyle(CyNetwork network) {

        CytoscapeDesktop cytoscapeDesktop = Cytoscape.getDesktop();
        VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
        CalculatorCatalog calculatorCatalog = vmm.getCalculatorCatalog();
        //gets the currently active visual style
        VisualStyle currentStyle = vmm.getVisualStyle();

        //methods to access the node, edge, and global appearance calculators
        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator(currentStyle.getNodeAppearanceCalculator());
        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator(currentStyle.getEdgeAppearanceCalculator());
        GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(currentStyle.getGlobalAppearanceCalculator());


		// create the visual style
        VisualStyle visualStyle = new VisualStyle(BINGO_VS, nodeAppCalc, edgeAppCalc, globalAppCalc);

		// update with BiNGO specific style
		adaptVisualStyle(visualStyle,network);

        return visualStyle;
    }
}


