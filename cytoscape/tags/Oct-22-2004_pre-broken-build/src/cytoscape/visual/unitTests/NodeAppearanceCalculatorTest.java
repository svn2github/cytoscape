// NodeAppearanceCalculatorTest.java

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
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

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
import cytoscape.util.GinyFactory;

import cytoscape.Cytoscape;
import cytoscape.data.GraphObjAttributes;
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
        Color fillColor = new Color(63, 128, 255);
        Color borderColor = new Color(100, 100, 50);
        LineType lineType = LineType.DASHED_3;
        byte shape = ShapeNodeRealizer.DIAMOND;
        double width = 49.0;
        double height = 79.0;
        String label = "testLabel";
        String toolTip = "testToolTip";
        Font font = new Font("SansSerif", Font.ITALIC, 10);
        
        RootGraph graph = Cytoscape.getRootGraph();
        int index1 = graph.createNode();
        Node first = graph.getNode(index1);
        int index2 = graph.createNode();
        Node second = graph.getNode(index2);
        
        GraphObjAttributes firstNodeAttr = Cytoscape.getNodeNetworkData();
        String firstName = "first node";
        firstNodeAttr.addNameMapping(firstName, first);
        firstNodeAttr.set("node.fillColor", firstName, fillColor);
        firstNodeAttr.set("node.borderColor", firstName, borderColor);
        firstNodeAttr.set("node.lineType", firstName, lineType);
        firstNodeAttr.set("node.shape", firstName, new Byte(shape));
        firstNodeAttr.set("node.width", firstName, width);
        firstNodeAttr.set("node.height", firstName, height);
        firstNodeAttr.set("node.label", firstName, label);
        firstNodeAttr.set("node.toolTip", firstName, toolTip);
        firstNodeAttr.set("node.font", firstName, font);
        
       
        
        CyNetwork network1 = Cytoscape.createNetwork( Cytoscape.
                                                      getRootGraph().
                                                      getNodeIndicesArray(),
                                                      Cytoscape.
                                                      getRootGraph().
                                                      getEdgeIndicesArray(),
                                                      null);
        
        NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
        nac.setNodeSizeLocked(false);
        
        NodeAppearance firstApp = nac.calculateNodeAppearance(first, network1);
        assertTrue( firstApp.getFillColor().equals(fillColor) );
        assertTrue( firstApp.getBorderColor().equals(borderColor) );
        //assertTrue( firstApp.getBorderLineType().equals(lineType) );
        assertTrue( firstApp.getShape() == shape );
        assertTrue( firstApp.getWidth() == width );
        assertTrue( firstApp.getHeight() == height );
        assertTrue( firstApp.getLabel().equals(label) );
        assertTrue( firstApp.getToolTip().equals(toolTip) );
        assertTrue( firstApp.getFont().equals(font) );
        

        CyNetwork network2 = Cytoscape.createNetwork( Cytoscape.
                                                      getRootGraph().
                                                      getNodeIndicesArray(),
                                                      Cytoscape.
                                                      getRootGraph().
                                                      getEdgeIndicesArray(),
                                                      null);

        GraphObjAttributes secondNodeAttr = Cytoscape.getNodeNetworkData();
        String secondName = "second node";
        secondNodeAttr.addNameMapping(secondName, second);
        secondNodeAttr.set("node.fillColor", secondName, "63,128,255");
        secondNodeAttr.set("node.borderColor", secondName, "100,100,50");
        secondNodeAttr.set("node.lineType", secondName, "dashed3");
        secondNodeAttr.set("node.shape", secondName, "diamond");
        secondNodeAttr.set("node.width", secondName, "49.0");
        secondNodeAttr.set("node.height", secondName, "79.0");
        secondNodeAttr.set("node.label", secondName, "testLabel");
        secondNodeAttr.set("node.toolTip", secondName, "testToolTip");
        secondNodeAttr.set("node.font", secondName, "SansSerif,italic,10");

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


