
/*
  File: NodeAppearanceCalculatorTest.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
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
import java.util.Properties;
import java.util.Map;

import giny.model.Node;
import giny.model.Edge;
import giny.model.RootGraph;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.CyNetwork;

import cytoscape.data.readers.CyAttributesReader;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.Semantics;
import cytoscape.util.FileUtil;

import cytoscape.visual.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.ui.*;
import cytoscape.visual.calculators.*;


public class NodeAppearanceCalculatorTest extends TestCase {

	CyNetwork cyNet;

	CyNode a;
	CyNode b;

	CyEdge ab;

	CalculatorCatalog catalog;
	Properties props;

    public NodeAppearanceCalculatorTest (String name) {super (name);}

    public void setUp() {
	cyNet = Cytoscape.createNetworkFromFile("testData/small.sif");
	a = Cytoscape.getCyNode("a");
	b = Cytoscape.getCyNode("b");
	ab = Cytoscape.getCyEdge(a,b,Semantics.INTERACTION,"pp",false);
	props = new Properties();
	try {
	CyAttributesReader.loadAttributes(Cytoscape.getNodeAttributes(),
                  new FileReader( "testData/small.nodeAttr"));
	props.load(FileUtil.getInputStream("testData/small.vizmap.props"));
	} catch(Exception e) { e.printStackTrace(); }
	catalog = new CalculatorCatalog();
	CalculatorIO.loadCalculators(props,catalog,true);
    }


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

    public void testDefaultAppearance() {

    	System.out.println("begin NodeAppearanceCalculatorTest.testDefaultAppearance()");

	NodeAppearanceCalculator nac = new NodeAppearanceCalculator(); 

	NodeAppearance na = nac.calculateNodeAppearance(a,cyNet);

	// this tests that the default node appearance is correct
	assertTrue( "color  " + na.getFillColor(), na.getFillColor().equals(Color.WHITE) );
	assertTrue( "border color  " + na.getBorderColor(), na.getBorderColor().equals(Color.BLACK) );
	assertTrue( "lineType  " + na.getBorderLineType(), na.getBorderLineType() == LineType.LINE_1 );
	assertTrue( "shape  " + na.getShape(), na.getShape() == ShapeNodeRealizer.RECT );
	assertTrue( "width  " + na.getWidth(), na.getWidth() == 30.0 );
	assertTrue( "height  " + na.getHeight(), na.getHeight() == 30.0 );
	assertTrue( "label  " + na.getLabel(), na.getLabel().equals("") );
	assertTrue( "tooltip  " + na.getToolTip(), na.getToolTip().equals("") );
	assertTrue( "font size  " + na.getFont().getSize(), na.getFont().getSize() == 12 ); 
	assertTrue( "font style  " + na.getFont().getStyle(), na.getFont().getStyle() == Font.PLAIN );
	assertTrue( "label color  " + na.getBorderColor(), na.getBorderColor().equals(Color.BLACK) );

	// should still be default for node b
	NodeAppearance nb = nac.calculateNodeAppearance(a,cyNet);

	assertTrue( "color  " + nb.getFillColor(), nb.getFillColor().equals(Color.WHITE) );
	assertTrue( "border color  " + nb.getBorderColor(), nb.getBorderColor().equals(Color.BLACK) );
	assertTrue( "lineType  " + nb.getBorderLineType(), nb.getBorderLineType() == LineType.LINE_1 );
	assertTrue( "shape  " + nb.getShape(), nb.getShape() == ShapeNodeRealizer.RECT );
	assertTrue( "width  " + nb.getWidth(), nb.getWidth() == 30.0 );
	assertTrue( "height  " + nb.getHeight(), nb.getHeight() == 30.0 );
	assertTrue( "label  " + nb.getLabel(), nb.getLabel().equals("") );
	assertTrue( "tooltip  " + nb.getToolTip(), nb.getToolTip().equals("") );
	assertTrue( "font size  " + nb.getFont().getSize(), nb.getFont().getSize() == 12 ); 
	assertTrue( "font style  " + nb.getFont().getStyle(), nb.getFont().getStyle() == Font.PLAIN );
	assertTrue( "label color  " + nb.getBorderColor(), nb.getBorderColor().equals(Color.BLACK) );

    	System.out.println("end NodeAppearanceCalculatorTest.testDefaultAppearance()");
    }

    public void testApplyProperties() {

    	System.out.println("begin NodeAppearanceCalculatorTest.testApplyProperties()");

	NodeAppearanceCalculator nac = new NodeAppearanceCalculator(); 
	nac.applyProperties("homer",props,"nodeAppearanceCalculator.homer",catalog);
	System.out.println(nac.getDescription());

	// node a
	NodeAppearance na = nac.calculateNodeAppearance(a,cyNet);

	System.out.println( "a color  " + na.getFillColor() );
	System.out.println( "a border color  " + na.getBorderColor() );
	System.out.println( "a lineType  " + na.getBorderLineType() );
	System.out.println( "a shape  " + na.getShape() );
	System.out.println( "a width  " + na.getWidth() );
	System.out.println( "a height  " + na.getHeight() );
	System.out.println( "a label  " + na.getLabel() );
	System.out.println( "a tooltip  " + na.getToolTip() );
	System.out.println( "a font size  " + na.getFont().getSize() );
	System.out.println( "a font style  " + na.getFont().getStyle() );
	System.out.println( "a label color  " + na.getBorderColor() );

	assertTrue( "a color  " + na.getFillColor(), na.getFillColor().equals(new Color(246,242,103)) );
	assertTrue( "a border color  " + na.getBorderColor(), na.getBorderColor().equals(Color.BLACK) );
	assertTrue( "a lineType  " + na.getBorderLineType(), na.getBorderLineType() == LineType.LINE_1 );
	assertTrue( "a shape  " + na.getShape(), na.getShape() == ShapeNodeRealizer.RECT );
	assertTrue( "a width  " + na.getWidth(), na.getWidth() == 10.0 );
	assertTrue( "a height  " + na.getHeight(), na.getHeight() == 10.0 );
	assertTrue( "a label  " + na.getLabel(), na.getLabel().equals("a") );
	assertTrue( "a tooltip  " + na.getToolTip(), na.getToolTip().equals("") );
	assertTrue( "a font size  " + na.getFont().getSize(), na.getFont().getSize() == 12 ); 
	assertTrue( "a font style  " + na.getFont().getStyle(), na.getFont().getStyle() == Font.PLAIN );
	assertTrue( "a label color  " + na.getLabelColor(), na.getLabelColor().equals(Color.BLACK) );

	// node b
	NodeAppearance nb = nac.calculateNodeAppearance(b,cyNet);

	System.out.println( "b color  " + nb.getFillColor() );
	System.out.println( "b border color  " + nb.getBorderColor() );
	System.out.println( "b lineType  " + nb.getBorderLineType() );
	System.out.println( "b shape  " + nb.getShape() );
	System.out.println( "b width  " + nb.getWidth() );
	System.out.println( "b height  " + nb.getHeight() );
	System.out.println( "b label  " + nb.getLabel() );
	System.out.println( "b tooltip  " + nb.getToolTip() );
	System.out.println( "b font size  " + nb.getFont().getSize() );
	System.out.println( "b font style  " + nb.getFont().getStyle() );
	System.out.println( "b label color  " + nb.getBorderColor() );

	assertTrue( "b color  " + nb.getFillColor(), nb.getFillColor().equals(new Color(87,25,230)) );
	assertTrue( "b border color  " + nb.getBorderColor(), nb.getBorderColor().equals(Color.BLACK) );
	assertTrue( "b lineType  " + nb.getBorderLineType(), nb.getBorderLineType() == LineType.LINE_5 );
	assertTrue( "b shape  " + nb.getShape(), nb.getShape() == ShapeNodeRealizer.RECT );
	assertTrue( "b width  " + nb.getWidth(), nb.getWidth() == 30.0 );
	assertTrue( "b height  " + nb.getHeight(), nb.getHeight() == 30.0 );
	assertTrue( "b label  " + nb.getLabel(), nb.getLabel().equals("b") );
	assertTrue( "b tooltip  " + nb.getToolTip(), nb.getToolTip().equals("") );
	assertTrue( "b font size  " + nb.getFont().getSize(), nb.getFont().getSize() == 12 ); 
	assertTrue( "b font style  " + nb.getFont().getStyle(), nb.getFont().getStyle() == Font.PLAIN );
	assertTrue( "b label color  " + nb.getLabelColor(), nb.getLabelColor().equals(Color.BLACK) );

    	System.out.println("end NodeAppearanceCalculatorTest.testApplyProperties()");
    }

    public static void main (String [] args) {
	junit.textui.TestRunner.run (new TestSuite (NodeAppearanceCalculatorTest.class));
    }
}


