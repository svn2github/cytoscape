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
		java.util.Calendar cal = java.util.Calendar.getInstance();
		System.out.println(cal.getTimeInMillis());
		// create 10k nodes and do what exactly??
		Network network = new NetworkImpl("test");

		
		Node[] connectingNodes = new Node[5];
		for (int i=0; i<10000; i++) {
			Node node = network.addNode(null);
			System.out.println("Adding node " + i);
			
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
				network.addEdge(node, connectingNodes[j], null);
		}
			

		}
		
		System.out.println(network.getNodes().length);

		int totalEdges = 0;
		for (Node n: network.getNodes()) {
			totalEdges += n.getAdjacentEdges().length;
		}
		
		System.out.println("" + totalEdges);
		
		java.util.Calendar cal2 = java.util.Calendar.getInstance();
		System.out.println("START: " + cal.getTimeInMillis());

		System.out.println("END: " + cal2.getTimeInMillis());

	}

}
