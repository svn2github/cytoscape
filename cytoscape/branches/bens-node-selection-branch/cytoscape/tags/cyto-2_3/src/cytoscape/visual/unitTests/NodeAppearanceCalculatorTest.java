
/*
  File: NodeAppearanceCalculatorTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// NodeAppearanceCalculatorTest.java


//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.unitTests;
//----------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;

import java.awt.Color;
import java.awt.Font;

import giny.model.Node;
import giny.model.RootGraph;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.LineType;
import cytoscape.visual.ShapeNodeRealizer;
//----------------------------------------------------------------------------
public class NodeAppearanceCalculatorTest extends TestCase {

//----------------------------------------------------------------------------
    public NodeAppearanceCalculatorTest (String name) {super (name);}
//----------------------------------------------------------------------------
    public void setUp () throws Exception {}
//----------------------------------------------------------------------------
    public void tearDown () throws Exception {}
//----------------------------------------------------------------------------
    public void testBypass () throws Exception {

	// The first tests here are really redundant because we no longer
	// allow arbitrary objects to be set as attributes, therefore the
	// second tests below are really the only test needed.  However, I'm
	// leaving the first tests here for the hell of it.
        Color fillColor = new Color(63, 128, 255);
        Color borderColor = new Color(100, 100, 50);
	String fillColorString = "63,128,255"; 
	String borderColorString = "100,100,50";
        LineType lineType = LineType.DASHED_3;
        String lineTypeString = "DASHED_3";
        byte shape = ShapeNodeRealizer.DIAMOND;
        String shapeString = "DIAMOND";
        double width = 49.0;
        double height = 79.0;
        String label = "testLabel";
        String toolTip = "testToolTip";
        Font font = new Font("SansSerif", Font.ITALIC, 10);
        String fontString = "SansSerif,italic,10";
        
        RootGraph graph = Cytoscape.getRootGraph();
        int index1 = graph.createNode();
        Node first = graph.getNode(index1);
        int index2 = graph.createNode();
        Node second = graph.getNode(index2);
        
        CyAttributes firstNodeAttr = Cytoscape.getNodeAttributes();
        String firstName = first.getIdentifier(); 
        firstNodeAttr.setAttribute(firstName, "node.fillColor", fillColorString);
        firstNodeAttr.setAttribute(firstName, "node.borderColor", borderColorString);
        firstNodeAttr.setAttribute(firstName, "node.lineType", lineTypeString);
        firstNodeAttr.setAttribute(firstName, "node.shape", shapeString);
        firstNodeAttr.setAttribute(firstName, "node.width", "49.0");
        firstNodeAttr.setAttribute(firstName, "node.height", "79.0");
        firstNodeAttr.setAttribute(firstName, "node.label", label);
        firstNodeAttr.setAttribute(firstName, "node.toolTip", toolTip);
        firstNodeAttr.setAttribute(firstName, "node.font", fontString);
        
       
        
        CyNetwork network1 = Cytoscape.createNetwork( Cytoscape.getRootGraph().getNodeIndicesArray(),
                                                      Cytoscape.getRootGraph().getEdgeIndicesArray(),
                                                      null);
        
        NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
        nac.setNodeSizeLocked(false);
        
        NodeAppearance firstApp = nac.calculateNodeAppearance(first, network1);
        assertTrue( firstApp.getFillColor().equals(fillColor) );
        assertTrue( firstApp.getBorderColor().equals(borderColor) );
        assertTrue( firstApp.getBorderLineType().equals(lineType) );
        assertTrue( firstApp.getShape() == shape );
        assertTrue( firstApp.getWidth() == width );
        assertTrue( firstApp.getHeight() == height );
        assertTrue( firstApp.getLabel().equals(label) );
        assertTrue( firstApp.getToolTip().equals(toolTip) );
        assertTrue( firstApp.getFont().equals(font) );
        

        CyNetwork network2 = Cytoscape.createNetwork( Cytoscape.getRootGraph().getNodeIndicesArray(),
                                                      Cytoscape.getRootGraph().getEdgeIndicesArray(),
                                                      null);

        CyAttributes secondNodeAttr = Cytoscape.getNodeAttributes();
        String secondName = second.getIdentifier(); 
        secondNodeAttr.setAttribute(secondName, "node.fillColor", "63,128,255");
        secondNodeAttr.setAttribute(secondName, "node.borderColor", "100,100,50");
        secondNodeAttr.setAttribute(secondName, "node.lineType", "dashed3");
        secondNodeAttr.setAttribute(secondName, "node.shape", "diamond");
        secondNodeAttr.setAttribute(secondName, "node.width", "49.0");
        secondNodeAttr.setAttribute(secondName, "node.height", "79.0");
        secondNodeAttr.setAttribute(secondName, "node.label", "testLabel");
        secondNodeAttr.setAttribute(secondName, "node.toolTip", "testToolTip");
        secondNodeAttr.setAttribute(secondName, "node.font", "SansSerif,italic,10");

        NodeAppearance secondApp = nac.calculateNodeAppearance(second, network2);
        assertTrue( secondApp.getFillColor().equals(fillColor) );
        assertTrue( secondApp.getBorderColor().equals(borderColor) );
        assertTrue( secondApp.getBorderLineType().equals(lineType) );
        assertTrue( secondApp.getShape() == shape );
        assertTrue( secondApp.getWidth() == width );
        assertTrue( secondApp.getHeight() == height );
        assertTrue( secondApp.getLabel().equals(label) );
        assertTrue( secondApp.getToolTip().equals(toolTip) );
        assertTrue( secondApp.getFont().equals(font) );
        
        nac.setNodeSizeLocked(true);
        nac.calculateNodeAppearance(firstApp, first, network1);
        assertTrue( firstApp.getWidth() == height );
        nac.calculateNodeAppearance(secondApp, second, network2);
        assertTrue( secondApp.getWidth() == height );
    }
//---------------------------------------------------------------------------
    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (NodeAppearanceCalculatorTest.class));
    }
//----------------------------------------------------------------------------
}


