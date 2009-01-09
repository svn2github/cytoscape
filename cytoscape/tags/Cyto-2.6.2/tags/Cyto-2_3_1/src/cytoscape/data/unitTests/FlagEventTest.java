
/*
  File: FlagEventTest.java 
  
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


//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import giny.model.*;
import cytoscape.Cytoscape;
import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEvent;
import cytoscape.unitTests.AllTests;
//------------------------------------------------------------------------------
public class FlagEventTest extends TestCase {


//------------------------------------------------------------------------------
public FlagEventTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
}
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
/**
 * This method makes sure that all of the static contants defined in the class
 * have different values.
 */
public void testConstants() throws Exception {
    assertTrue(FlagEvent.SINGLE_NODE != FlagEvent.SINGLE_EDGE);
    assertTrue(FlagEvent.SINGLE_NODE != FlagEvent.NODE_SET);
    assertTrue(FlagEvent.SINGLE_NODE != FlagEvent.EDGE_SET);
    assertTrue(FlagEvent.SINGLE_EDGE != FlagEvent.NODE_SET);
    assertTrue(FlagEvent.SINGLE_EDGE != FlagEvent.EDGE_SET);
    assertTrue(FlagEvent.NODE_SET != FlagEvent.EDGE_SET);
}
//------------------------------------------------------------------------------
public void testCtor () throws Exception { 
  RootGraph rootGraph = Cytoscape.getRootGraph();
    Node node1 = rootGraph.getNode(rootGraph.createNode());
    Node node2 = rootGraph.getNode(rootGraph.createNode());
    Edge edge1 = rootGraph.getEdge(rootGraph.createEdge(node1, node2));
    Edge edge2 = rootGraph.getEdge(rootGraph.createEdge(node2, node1));
    Node[] nodeArray = {node1, node2};
    Edge[] edgeArray = {edge1, edge2};
    GraphPerspective gp = rootGraph.createGraphPerspective(nodeArray, edgeArray);
    FlagFilter source = new FlagFilter(gp);
    Set nodeSet = new HashSet();
    nodeSet.add(node1);
    nodeSet.add(node2);
    Set edgeSet = new HashSet();
    edgeSet.add(edge1);
    edgeSet.add(edge2);
    
    FlagEvent singleNodeOn = new FlagEvent(source, node1, true);
    checkEvent(singleNodeOn, source, node1, FlagEvent.SINGLE_NODE, true);
    FlagEvent singleNodeOff = new FlagEvent(source, node2, false);
    checkEvent(singleNodeOff, source, node2, FlagEvent.SINGLE_NODE, false);
    FlagEvent singleEdgeOn = new FlagEvent(source, edge1, true);
    checkEvent(singleEdgeOn, source, edge1, FlagEvent.SINGLE_EDGE, true);
    FlagEvent singleEdgeOff = new FlagEvent(source, edge2, false);
    checkEvent(singleEdgeOff, source, edge2, FlagEvent.SINGLE_EDGE, false);
    FlagEvent nodeSetOn = new FlagEvent(source, nodeSet, true);
    checkEvent(nodeSetOn, source, nodeSet, FlagEvent.NODE_SET, true);
    FlagEvent nodeSetOff = new FlagEvent(source, nodeSet, false);
    checkEvent(nodeSetOff, source, nodeSet, FlagEvent.NODE_SET, false);
    FlagEvent edgeSetOn = new FlagEvent(source, edgeSet, true);
    checkEvent(edgeSetOn, source, edgeSet, FlagEvent.EDGE_SET, true);
    FlagEvent edgeSetOff = new FlagEvent(source, edgeSet, false);
    checkEvent(edgeSetOff, source, edgeSet, FlagEvent.EDGE_SET, false);
} // testCtor
//-------------------------------------------------------------------------
public void checkEvent(FlagEvent event, FlagFilter source, Object target,
                       int targetType, boolean selectOn) {
    assertTrue(event.getSource() == source);
    assertTrue(event.getTarget() == target);
    assertTrue(event.getTargetType() == targetType);
    assertTrue(event.getEventType() == selectOn);
}
//-------------------------------------------------------------------------
public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (FlagEventTest.class));
}
//------------------------------------------------------------------------------
}


