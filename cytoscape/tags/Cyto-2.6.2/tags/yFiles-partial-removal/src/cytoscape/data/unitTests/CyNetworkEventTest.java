//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.*;

import cytoscape.data.CyNetwork;
import cytoscape.data.CyNetworkEvent;
//-----------------------------------------------------------------------------------------
public class CyNetworkEventTest extends TestCase {
//------------------------------------------------------------------------------
public CyNetworkEventTest(String name) {super(name);}
//------------------------------------------------------------------------------
public void setUp() throws Exception {}
//------------------------------------------------------------------------------
public void tearDown() throws Exception {}
//------------------------------------------------------------------------------
public void testBasic() throws Exception { 
    CyNetwork network = new CyNetwork();
    //test begin event
    CyNetworkEvent e0 = new CyNetworkEvent(network, CyNetworkEvent.BEGIN);
    assertTrue( e0.getNetwork() == network );
    assertTrue( e0.getType() == CyNetworkEvent.BEGIN );
    //test end event
    CyNetworkEvent e1 = new CyNetworkEvent(network, CyNetworkEvent.END);
    assertTrue( e1.getNetwork() == network );
    assertTrue( e1.getType() == CyNetworkEvent.END );
    //test null network
    CyNetworkEvent eNull = new CyNetworkEvent(null, CyNetworkEvent.BEGIN);
    assertTrue( eNull.getNetwork() == null );
    assertTrue( eNull.getType() == CyNetworkEvent.BEGIN );
    //test graph replaced event
    CyNetworkEvent e2 = new CyNetworkEvent(network, CyNetworkEvent.GRAPH_REPLACED);
    assertTrue( e2.getNetwork() == network );
    assertTrue( e2.getType() == CyNetworkEvent.GRAPH_REPLACED );
    //test unknown event
    CyNetworkEvent e3 = new CyNetworkEvent(network, CyNetworkEvent.UNKNOWN);
    assertTrue( e3.getNetwork() == network );
    assertTrue( e3.getType() == CyNetworkEvent.UNKNOWN );
    //test invalid type
    CyNetworkEvent eBad = new CyNetworkEvent(network, -7);
    assertTrue( eBad.getNetwork() == network );
    assertTrue( eBad.getType() == CyNetworkEvent.UNKNOWN );
}
//-------------------------------------------------------------------------
public static void main (String[] args)  {
    junit.textui.TestRunner.run(new TestSuite(CyNetworkEventTest.class));
}
//-------------------------------------------------------------------------
}

