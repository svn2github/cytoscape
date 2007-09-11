package oiler.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException; 
import java.io.FileNotFoundException; 

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import oiler.Graph;
import oiler.LinkedListGraph;

/**
 * A class that parses SIF files.
 *
 * @author Samad Lotia
 */
public class SIFReader
{
	protected SIFReader() {}

	/**
	 * Parses the file given by <code>inputFilePath</code>.
	 */
	public static Graph<String,String> read(String inputFilePath) throws IOException, FileNotFoundException
	{
		return read(new BufferedReader(new FileReader(inputFilePath)), true);
	}

	/**
	 * Parses the file given by <code>input</code>. It will
	 * close the reader when finished parsing.
	 */
	public static Graph<String,String> read(BufferedReader input) throws IOException
	{
		return read(input, true);
	}
	
	/**
	 * Parses a SIF file.
	 * @param input the reader to read the SIF file from
	 * @param closeWhenFinished if true, this method will close
	 *        the reader when finished reading; otherwise, it will
	 *        not close it.
	 */
	public static Graph<String,String> read(BufferedReader input, boolean closeWhenFinished) throws IOException
	{
		Graph<String,String> graph = new LinkedListGraph<String,String>();
		HashMap<String,Integer> nodeNameToIndexMap = new HashMap<String,Integer>();
		Vector<String> sifLine = new Vector<String>();
		Pattern splitter = Pattern.compile("\\s+");
		String line;

		while ((line = input.readLine()) != null)
		{
			String[] tokens = splitter.split(line);
			for (int i = 0; i < tokens.length; i++)
				if (tokens[i].length() != 0)
					sifLine.add(tokens[i]);
			parseLine(graph, nodeNameToIndexMap, sifLine);
		}
		
		if (closeWhenFinished)
			input.close();
		return graph;
	}

	private static void parseLine(	Graph<String,String> graph,
					HashMap<String,Integer> nodeNameToIndexMap,
					Vector<String> sifLine) throws IOException
	{
		if (sifLine.size() == 0)
			return;
		else if (sifLine.size() == 1)
		{
			String name = sifLine.get(0);
			int node = nodeIndex(graph, name, nodeNameToIndexMap);
			graph.addEdge(node, node, null, Graph.UNDIRECTED_EDGE);
		}
		else if (sifLine.size() == 2)
			throw new IOException("Expected node name, found end of line");
		else
		{
			String node0Name = sifLine.get(0);
			int node0 = nodeIndex(graph, node0Name, nodeNameToIndexMap);
			String interaction = sifLine.get(1);

			for (int i = 2; i < sifLine.size(); i++)
			{
				String nodeNName = sifLine.get(i);
				int nodeN = nodeIndex(graph, nodeNName, nodeNameToIndexMap);
				graph.addEdge(node0, nodeN, interaction, Graph.UNDIRECTED_EDGE);
			}
		}

		sifLine.clear();
	}

	private static int nodeIndex(Graph<String,String> graph, String name, HashMap<String,Integer> nodeNameToIndexMap)
	{
		Integer result = nodeNameToIndexMap.get(name);
		if (result == null)
		{
			result = graph.addNode(name);
			nodeNameToIndexMap.put(name, result);
		}
		return result;
	}
}
