package DenovoPGNetworkAlignmentPlugin;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.StringReader;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;


public class CyNetworkConverter
{
	public static BufferedReader convert(CyNetwork network)
	{
		StringBuffer buffer = new StringBuffer();
		Iterator edges = network.edgesIterator();
		while (edges.hasNext())
		{
			CyEdge edge = (CyEdge) edges.next();
			String sourceID = edge.getSource().getIdentifier();
			String targetID = edge.getTarget().getIdentifier();
			buffer.append(sourceID);
			buffer.append("\tpp\t");
			buffer.append(targetID);
			buffer.append("\n");
		}
		return new BufferedReader(new StringReader(buffer.toString()));
	}
}
