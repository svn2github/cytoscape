package cytoscape.data.unitTests;

import junit.framework.*;
import java.io.*;
import java.util.*;

import giny.model.*;
import cytoscape.Cytoscape;
import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;
import cytoscape.unitTests.AllTests;


public class FlagFilterTest extends TestCase {

    FlagFilter filter;
    Node node1;
    Node node2;
    Node otherNode;
    Edge edge1;
    Edge edge2;
    Edge otherEdge;
    GraphPerspective gp;
    ListenerTest listener;
    FlagEvent savedEvent;


public FlagFilterTest (String name) 
{
    super (name);
}


public void setUp () throws Exception
{
  RootGraph rootGraph = Cytoscape.getRootGraph();
    node1 = rootGraph.getNode(rootGraph.createNode());
    node2 = rootGraph.getNode(rootGraph.createNode());
    edge1 = rootGraph.getEdge(rootGraph.createEdge(node1, node2));
    edge2 = rootGraph.getEdge(rootGraph.createEdge(node2, node1));
    Node[] nodeArray = {node1, node2};
    Edge[] edgeArray = {edge1, edge2};
    gp = rootGraph.createGraphPerspective(nodeArray, edgeArray);
    //some objects not in this GraphPerspective
    otherNode = rootGraph.getNode(rootGraph.createNode());
    otherEdge = rootGraph.getEdge(rootGraph.createEdge(node1, otherNode));
    filter = new FlagFilter(gp);
    listener = new ListenerTest();
    filter.addFlagEventListener(listener);
}


public void tearDown () throws Exception
{
}
//-------------------------------------------------------------------------
/**
 * Checks that the current state of the filter matches the state described
 * by the arguments.
 */
public void checkState(boolean n1, boolean n2, boolean e1, boolean e2) {
    assertTrue( filter.isFlagged(node1) == n1 );
    assertTrue( filter.isFlagged(node2) == n2 );
    assertTrue( filter.isFlagged(edge1) == e1 );
    assertTrue( filter.isFlagged(edge2) == e2 );
    assertTrue( filter.passesFilter(node1) == n1 );
    assertTrue( filter.passesFilter(node2) == n2 );
    assertTrue( filter.passesFilter(edge1) == e1 );
    assertTrue( filter.passesFilter(edge2) == e2 );
    if (n1) {assertTrue( filter.getFlaggedNodes().contains(node1) );}
    if (n2) {assertTrue( filter.getFlaggedNodes().contains(node2) );}
    if (e1) {assertTrue( filter.getFlaggedEdges().contains(edge1) );}
    if (e2) {assertTrue( filter.getFlaggedEdges().contains(edge2) );}
    int nodeCount = 0;
    if (n1) {nodeCount++;}
    if (n2) {nodeCount++;}
    assertTrue( filter.getFlaggedNodes().size() == nodeCount );
    int edgeCount = 0;
    if (e1) {edgeCount++;}
    if (e2) {edgeCount++;}
    assertTrue( filter.getFlaggedEdges().size() == edgeCount );
}
//-------------------------------------------------------------------------
/**
 * Checks that the most recently fired event matches the supplied arguments.
 */
public void checkEvent(Object target, int targetType, boolean flagOn) {
    FlagEvent event = listener.getEvent();
    assertTrue(event.getSource() == filter);
    assertTrue(event.getTarget() == target);
    assertTrue(event.getTargetType() == targetType);
    assertTrue(event.getEventType() == flagOn);
}


public void testSingleFlags () throws Exception { 
    checkState(false, false, false, false);
    
    filter.setFlagged(node1, true);
    checkState(true, false, false, false);
    checkEvent(node1, FlagEvent.SINGLE_NODE, true);
    
    filter.setFlagged(edge1, true);
    checkState(true, false, true, false);
    checkEvent(edge1, FlagEvent.SINGLE_EDGE, true);
    
    filter.setFlagged(node2, true);
    checkState(true, true, true, false);
    checkEvent(node2, FlagEvent.SINGLE_NODE, true);
    
    filter.setFlagged(edge2, true);
    checkState(true, true, true, true);
    checkEvent(edge2, FlagEvent.SINGLE_EDGE, true);
    
    filter.setFlagged(edge1, false);
    checkState(true, true, false, true);
    checkEvent(edge1, FlagEvent.SINGLE_EDGE, false);
    
    filter.setFlagged(node1, false);
    checkState(false, true, false, true);
    checkEvent(node1, FlagEvent.SINGLE_NODE, false);
    
    savedEvent = listener.getEvent();
    filter.setFlagged(edge2, true); //should do nothing
    checkState(false, true, false, true);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    filter.setFlagged(node2, true); //should do nothing
    checkState(false, true, false, true);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    
    filter.setFlagged(edge2, false);
    checkState(false, true, false, false);
    checkEvent(edge2, FlagEvent.SINGLE_EDGE, false);
    
    filter.setFlagged(node2, false);
    checkState(false, false, false, false);
    checkEvent(node2, FlagEvent.SINGLE_NODE, false);
    
    savedEvent = listener.getEvent();
    filter.setFlagged(node1, false); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    filter.setFlagged(edge1, false); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    
    //test objects not in this perspective
    /* these tests embargoed due to a bug in GraphPerspective.containsNode
    filter.setFlagged(otherNode, true); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    filter.setFlagged(otherNode, false); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    filter.setFlagged(otherEdge, true); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    filter.setFlagged(otherEdge, false); //should do nothing
    checkState(false, false, false, false);
    assertTrue( listener.getEvent() == savedEvent ); //no event should have been fired
    */
} // testCtor
//-------------------------------------------------------------------------
public void testGroupFlags() {
    Set testSet = null; //return value from filter methods
    checkState(false, false, false, false);
    
    Set nodeSet1 = new HashSet();
    nodeSet1.add(node1);
    testSet = filter.setFlaggedNodes(nodeSet1, true);
    checkState(true, false, false, false);
    checkEvent(testSet, FlagEvent.NODE_SET, true);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(node1) );
    
    Set edgeSet1 = new HashSet();
    edgeSet1.add(edge2);
    testSet = filter.setFlaggedEdges(edgeSet1, true);
    checkState(true, false, false, true);
    checkEvent(testSet, FlagEvent.EDGE_SET, true);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(edge2) );
    
    Set nodeSet2 = new HashSet();
    nodeSet2.add(node1);
    nodeSet2.add(node2);
    testSet = filter.setFlaggedNodes(nodeSet2, true);
    checkState(true, true, false, true);
    checkEvent(testSet, FlagEvent.NODE_SET, true);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(node2) );
    
    Set edgeSet2 = new HashSet();
    edgeSet2.add(edge1);
    edgeSet2.add(edge2);
    testSet = filter.setFlaggedEdges(edgeSet2, true);
    checkState(true, true, true, true);
    checkEvent(testSet, FlagEvent.EDGE_SET, true);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(edge1) );
    
    testSet = filter.setFlaggedNodes(nodeSet1, false);
    checkState(false, true, true, true);
    checkEvent(testSet, FlagEvent.NODE_SET, false);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(node1) );
    
    testSet = filter.setFlaggedEdges(edgeSet1, false);
    checkState(false, true, true, false);
    checkEvent(testSet, FlagEvent.EDGE_SET, false);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(edge2) );
    
    testSet = filter.setFlaggedNodes(nodeSet2, false);
    checkState(false, false, true, false);
    checkEvent(testSet, FlagEvent.NODE_SET, false);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(node2) );
    
    testSet = filter.setFlaggedEdges(edgeSet2, false);
    checkState(false, false, false, false);
    checkEvent(testSet, FlagEvent.EDGE_SET, false);
    assertTrue( testSet.size() == 1 );
    assertTrue( testSet.contains(edge1) );
    
    testSet = filter.setFlaggedNodes(nodeSet2, true);
    checkState(true, true, false, false);
    checkEvent(testSet, FlagEvent.NODE_SET, true);
    assertTrue( testSet.size() == 2 );
    assertTrue( testSet.contains(node1) );
    assertTrue( testSet.contains(node2) );
    
    testSet = filter.setFlaggedNodes(nodeSet2, false);
    checkState(false, false, false, false);
    checkEvent(testSet, FlagEvent.NODE_SET, false);
    assertTrue( testSet.size() == 2 );
    assertTrue( testSet.contains(node1) );
    assertTrue( testSet.contains(node2) );
    
    testSet = filter.setFlaggedEdges(edgeSet2, true);
    checkState(false, false, true, true);
    checkEvent(testSet, FlagEvent.EDGE_SET, true);
    assertTrue( testSet.size() == 2 );
    assertTrue( testSet.contains(edge1) );
    assertTrue( testSet.contains(edge2) );
    
    testSet = filter.setFlaggedEdges(edgeSet2, false);
    checkState(false, false, false, false);
    checkEvent(testSet, FlagEvent.EDGE_SET, false);
    assertTrue( testSet.size() == 2 );
    assertTrue( testSet.contains(edge1) );
    assertTrue( testSet.contains(edge2) );
}
//-------------------------------------------------------------------------
public void testFlagAll() {
    checkState(false, false, false, false);
    
    filter.flagAllNodes();
    checkState(true, true, false, false);
    checkFlagAllEvent(2, FlagEvent.NODE_SET, true);
    filter.unflagAllNodes();
    checkState(false, false, false, false);
    checkFlagAllEvent(2, FlagEvent.NODE_SET, false);
    filter.flagAllEdges();
    checkState(false, false, true, true);
    checkFlagAllEvent(2, FlagEvent.EDGE_SET, true);
    filter.unflagAllEdges();
    checkState(false, false, false, false);
    checkFlagAllEvent(2, FlagEvent.EDGE_SET, false);
    
    filter.setFlagged(node1, true);
    checkState(true, false, false, false);
    filter.flagAllNodes();
    checkState(true, true, false, false);
    checkFlagAllEvent(1, FlagEvent.NODE_SET, true);
    filter.setFlagged(node1, false);
    checkState(false, true, false, false);
    filter.unflagAllNodes();
    checkState(false, false, false, false);
    checkFlagAllEvent(1, FlagEvent.NODE_SET, false);
    
    filter.setFlagged(edge1, true);
    checkState(false, false, true, false);
    filter.flagAllEdges();
    checkState(false, false, true, true);
    checkFlagAllEvent(1, FlagEvent.EDGE_SET, true);
    filter.setFlagged(edge1, false);
    checkState(false, false, false, true);
    filter.unflagAllEdges();
    checkState(false, false, false, false);
    checkFlagAllEvent(1, FlagEvent.EDGE_SET, false);
}

/**
 * The event fired in response to a flagAll or unflagAll method call contains
 * a Set that we don't have a reference to. This method tests the contents of
 * that Set in addition to the other event parameters.
 */
public void checkFlagAllEvent(int setSize, int targetType, boolean flagOn) {

  //TODO: FIX
  
 //  FlagEvent event = listener.getEvent();
//     assertTrue( event.getSource() == filter );
//     assertTrue( event.getTarget() instanceof Set );
//     Set targetSet = (Set)event.getTarget();
//     assertTrue( targetSet.size() == setSize );
//     assertTrue( event.getTargetType() == targetType );
//     assertTrue( event.getEventType() == flagOn );
}

/**
 * Test the add and remove listener methods, as well as responding to object
 * removal in the underlying GraphPerspective.
 */
public void testListeners() {
    checkState(false, false, false, false);
    filter.setFlagged(node1, true);
    checkEvent(node1, FlagEvent.SINGLE_NODE, true);
    savedEvent = listener.getEvent();
    filter.removeFlagEventListener(listener);
    filter.setFlagged(node1, false);
    //this should be the same event since the listener is detached
    assertTrue( listener.getEvent() == savedEvent );
    checkEvent(node1, FlagEvent.SINGLE_NODE, true);
    filter.setFlagged(edge1, true);
    assertTrue( listener.getEvent() == savedEvent );
    checkEvent(node1, FlagEvent.SINGLE_NODE, true);
    filter.addFlagEventListener(listener);
    filter.setFlagged(edge1, false);
    assertTrue( listener.getEvent() != savedEvent );
    checkEvent(edge1, FlagEvent.SINGLE_EDGE, false);
     
    checkState(false, false, false, false);
    filter.flagAllNodes();
    filter.flagAllEdges();
    checkState(true, true, true, true);
    gp.hideEdge(edge1);
    checkState(true, true, false, true);
    checkFlagAllEvent(1, FlagEvent.EDGE_SET, false);
    savedEvent = listener.getEvent();
    gp.restoreEdge(edge1); //shouldn't change flagged state or fire an event
    checkState(true, true, false, true);
    assertTrue( listener.getEvent() == savedEvent );
    filter.unflagAllNodes();
    filter.unflagAllEdges();
    
    checkState(false, false, false, false);
    filter.flagAllNodes();
    filter.flagAllEdges();
    checkState(true, true, true, true);
    gp.hideNode(node1);  //implicitly hides both edges
    checkState(false, true, false, false);
    //two events get fired, we only catch the second one for the edges
    checkFlagAllEvent(2, FlagEvent.EDGE_SET, false);
    savedEvent = listener.getEvent();
    gp.restoreNode(node1);  //shouldn't change flagged state or fire an event
    checkState(false, true, false, false);
    assertTrue( listener.getEvent() == savedEvent );
    filter.unflagAllNodes();
    filter.unflagAllEdges();
}

public static void main (String[] args) 
{
  junit.textui.TestRunner.run (new TestSuite (FlagFilterTest.class));
}


private class ListenerTest implements FlagEventListener {
    private FlagEvent event;
    
    public void onFlagEvent(FlagEvent newEvent) {event = newEvent;}
    public FlagEvent getEvent() {return event;}
}


}


