
/*
  File: RandomRenderableSubgraphLogic.java 
  
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

package cytoscape.foo;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CyNetworkNaming;
import cytoscape.view.CyNetworkView;
import java.util.Random;
import java.util.Vector;

public class RandomRenderableSubgraphLogic
{

  public static void justDoIt(CyNetwork foo)
  {
    final int threshold = CytoscapeInit.getViewThreshold();
    final CyNetwork currentNetwork = foo;
    final int currentNodeCount = currentNetwork.getNodeCount();
//     if (currentNodeCount < threshold)
//       throw new IllegalStateException
//         ("misusage of this class - only use it if the number of nodes " +
//          "in the current network is equal to or exceeds the threshold");
    Vector inx = new Vector();

    // option 1:

    // get the array of nodes from the perspective
    int[] current_nodes = foo.getNodeIndicesArray();
    
    for (int i = 0; i < current_nodes.length; i++)
      inx.addElement( new Integer( current_nodes[i] ) );


    ////////////////////////////////////////
    // option 2:
    // indexing starts at 1 ... its a long story...
    //for (int i = 1; i <= currentNodeCount; i++)
    //   inx.addElement( new Integer( current_nodes[i] ) );
    ////////////////////////////////////////

    Random r = new Random();

    while ( inx.size() >= threshold )
      inx.removeElementAt( r.nextInt(inx.size() ) );

    final int[] nodeInx = new int[inx.size()];

    for (int i = 0; i < nodeInx.length; i++)
      nodeInx[i] = ((Integer) inx.elementAt(i)).intValue();

    CyNetwork newNetwork = Cytoscape.createNetwork
      (nodeInx, currentNetwork.getConnectingEdgeIndicesArray(nodeInx),
       CyNetworkNaming.getSuggestedSubnetworkTitle(currentNetwork),
       currentNetwork);

    newNetwork.setExpressionData(currentNetwork.getExpressionData());

    CyNetworkView newView =
      Cytoscape.getNetworkView(newNetwork.getIdentifier());
  }

}
