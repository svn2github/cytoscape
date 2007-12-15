/**
 * 
 */
package profile;

import org.cytoscape.model.*;
import org.cytoscape.model.impl.*;

/**
 * @author skillcoy
 *
 */
public class ProfileMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// create 10k nodes and do what exactly??
		CyNetwork network = new CyNetworkImpl("test");

		
		CyNode[] connectingNodes = new CyNode[5];
		for (int i=0; i<100000; i++) {
			CyNode node = network.addNode();

		if (i%5 == 0)
			connectingNodes[0] = node;
		if (i%10 == 0)
			connectingNodes[1] = node;
		if (i%20 == 0)
			connectingNodes[2] = node;
		if (i%50 == 0)
			connectingNodes[3] = node;
		if (i%100 == 0)
			connectingNodes[4] = node;
		
		for (int j=0; j<connectingNodes.length; j++) {
			if (connectingNodes[j] != null)
				node.connectTo(connectingNodes[j], false);
		}
			

		}
		
		System.out.println(network.getNodes().size());

		int totalEdges = 0;
		for (CyNode n: network.getNodes()) {
			totalEdges += n.getAdjacentEdges().size();
		}
		
		System.out.println("" + totalEdges);
	}

}
