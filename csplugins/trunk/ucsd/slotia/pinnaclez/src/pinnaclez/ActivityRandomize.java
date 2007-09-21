package pinnaclez;

import java.util.Random;
import oiler.Graph;
import oiler.util.IntArray;
import oiler.util.IntIterator;
import modlab.Randomize;

/**
 * Randomizes the matrixIndex value of all Activity objects
 * associated with each node in the network.
 * If an Activity object has an invalid matrixIndex
 * (in other words, it has a negative value), it will
 * not be randomized.
 */
public class ActivityRandomize implements Randomize<Activity,String>
{
	public void randomize(Graph<Activity,String> graph)
	{
		// Fill in the nodesList with the graph's node-indices
		final IntArray nodesList = new IntArray(graph.nodeCount()); 
		final IntIterator nodes = graph.nodes();
		for (int i = 0; nodes.hasNext(); i++)
			nodesList.set(i, nodes.next());
		
		final Random random = new Random();
		for (int i = 0; i < nodesList.size(); i++)
		{
			final int node = nodesList.get(i);

			// Skip nodes with an invalid matrix index
			if (graph.nodeObject(node).getMatrixIndex() < 0)
				continue;

			// Select another node that does not have an invalid matrix index
			int otherNode = -1;
			do
				otherNode = nodesList.get(random.nextInt(nodesList.size()));
			while (graph.nodeObject(otherNode).matrixIndex < 0);

			// Switch the two matrix indices
			final int temp = graph.nodeObject(node).matrixIndex;
			graph.nodeObject(node).matrixIndex = graph.nodeObject(otherNode).matrixIndex;
			graph.nodeObject(otherNode).matrixIndex = temp;
		}
	}
}
