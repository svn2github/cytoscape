package SawdPinnacleZ;

import java.util.*;
import pinnaclez.*;
import pinnaclez.io.*;
import modlab.*;
import oiler.*;
import oiler.util.*;

class Iteration
{
	public static boolean computeRandom(SawdClient client, Settings settings)
	{
		// Find the iteration that has not been completed

		int iteration = 0;
		while (iteration < settings.numOfTrials)
		{
			String status = client.get_global_attribute(States.ITERATION + Integer.toString(iteration));
			if (status.equals(States.ITERATION_EMPTY))
				break;
			iteration++;
		}

		// Check if no iterations are left
		if (iteration == settings.numOfTrials)
			return false;

		// Compute the iteration

		client.set_global_attribute(States.ITERATION + Integer.toString(iteration), States.ITERATION_RUNNING);

		System.err.print("Computing random iteration " + (iteration + 1) + " of " + settings.numOfTrials + "..."); System.err.flush();
		settings.randomize.randomize(settings.network);
		List<Graph<Activity,String>> results = settings.search.search(settings.network, settings.score);
		System.err.println(" done.");

		System.err.print("Sending results..."); System.err.flush();
		IntIntHashMap startNodes = getStartNodes(client);
		for (int i = 0; i < results.size(); i++)
		{
			Graph<Activity,String> graph = results.get(i);
			int startNode = graph.nodes().next();
			int graph_index = startNodes.get(startNode);
			client.set_graph_attribute(graph_index, "pinnaclez.score.random." + iteration, Double.toString(graph.score()));
		}
		System.err.println(" done.");

		client.set_global_attribute(States.ITERATION + Integer.toString(iteration), States.ITERATION_DONE);

		return true;
	}

	public static IntIntHashMap getStartNodes(SawdClient client)
	{
		IntIntHashMap map = new IntIntHashMap();
		int[] graph_indices = client.list_graphs();
		for (int i = 0; i < graph_indices.length; i++)
		{
			int graph_index = graph_indices[i];
			String startNodeValue = client.get_graph_attribute(graph_index, "pinnaclez.startNode");
			try
			{
				int startNode = Integer.parseInt(startNodeValue);
				map.put(startNode, graph_index);
			}
			catch (NumberFormatException e)
			{
				continue;
			}
		}
		return map;
	}

	public static void computeReal(SawdClient client, Settings settings)
	{
		client.set_global_attribute(States.ITERATION + "real", States.ITERATION_RUNNING);

		System.err.print("Computing real iteration..."); System.err.flush();
		List<Graph<Activity,String>> results = settings.search.search(settings.network, settings.score);
		System.err.println(" done.");

		System.err.println("Sending iteration...");
		ProgressBar progressBar = new ProgressBar(results.size());
		if (settings.verbose)
			progressBar.start();
		for (int i = 0; i < results.size(); i++)
		{
			Graph<Activity,String> graph = results.get(i);
			int graph_index = client.new_graph();
			int startNode = graph.nodes().next();
			client.set_graph_attribute(graph_index, "pinnaclez.startNode", Integer.toString(startNode));
			client.set_graph_attribute(graph_index, "pinnaclez.score.real", Double.toString(graph.score()));

			IntIntHashMap map = new IntIntHashMap();
			IntIterator nodes = graph.nodes();
			while (nodes.hasNext())
			{
				int node = nodes.next();
				int node_index = client.new_node(graph_index);
				map.put(node, node_index);
				client.set_node_attribute(graph_index, node_index, "name", Integer.toString(graph.nodeObject(node).matrixIndex));
			}

			IntIterator edges = graph.edges();
			while (edges.hasNext())
			{
				int edge = edges.next();
				int source = graph.edgeSource(edge);
				int source_index = map.get(source);
				int target = graph.edgeTarget(edge);
				int target_index = map.get(target);
				if (source_index == IntIntHashMap.KEY_NOT_FOUND || target_index == IntIntHashMap.KEY_NOT_FOUND)
					continue;
				client.new_edge(graph_index, source_index, target_index);
			}

			if (settings.verbose)
				progressBar.increment();
		}

		client.set_global_attribute(States.ITERATION + "real", States.ITERATION_DONE);
	}

	public static boolean setup(SawdClient client, Settings settings)
	{
		// Read class file

		if (settings.classMap == null)
		{
			if (settings.verbose) System.err.print("Reading class file..."); System.err.flush();
			try
			{
				settings.classMap = ClassReader.read(settings.classFilePath);
			}
			catch (ParsingException e)
			{
				System.err.println("Failed to read file: " + settings.classFilePath + "\n" + e.getMessage());
				return false;
			}
			if (settings.verbose) System.err.println(" done.");
		}

		// Read expression matrix file

		if (settings.matrix == null)
		{
			if (settings.verbose) System.err.print("Reading expression matrix file..."); System.err.flush();
			try
			{
				settings.matrix = ExpressionMatrixReader.read(settings.classMap, settings.matrixFilePath);
			}
			catch (ParsingException e)
			{
				System.err.println("Failed to read file: " + settings.matrixFilePath + "\n" + e.getMessage());
				return false;
			}
			if (settings.verbose) System.err.println(" done.");
		}

		// Read network file

		if (settings.network == null)
		{
			if (settings.verbose) System.err.print("Reading network file..."); System.err.flush();
			try
			{
				settings.network = NetworkReader.read(settings.matrix, settings.networkFilePath);
			}
			catch (ParsingException e)
			{
				System.err.println("Failed to read file: " + settings.networkFilePath + "\n" + e.getMessage());
				return false;
			}
			if (settings.verbose) System.err.println(" done.");
		}

		// Setup search algorithm

		if (settings.search == null)
			settings.search = new GreedySearch<Activity,String>(settings.maxModuleSize, settings.maxRadius, settings.maxNodeDegree, settings.minImprovement);

		// Setup score algorithm

		if (settings.score == null)
		{
			switch (settings.scoreModel)
			{
				case MI: settings.score = new MIScore(settings.matrix); break;
				case T: settings.score = new TScore(settings.matrix); break;
			}
		}

		// Setup randomizing algorithm

		if (settings.randomize == null)
			settings.randomize = new ActivityRandomize();
		return true;
	}
}
