package pinnaclez.io;

import java.util.Map;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.TypeConverter;
import oiler.io.SIFReader;

import pinnaclez.Activity;
import pinnaclez.ExpressionMatrix;

public class NetworkReader
{
	protected NetworkReader() {}

	public static Graph<Activity,String> read(ExpressionMatrix matrix, String networkFilePath) throws ParsingException
	{
		FileReader fileReader = null;
		try
		{
			fileReader = new FileReader(networkFilePath);
		}
		catch (FileNotFoundException e)
		{
			throw new ParsingException("Network", e.getMessage());
		}

		return read(matrix, new BufferedReader(fileReader));
	}

	public static Graph<Activity,String> read(ExpressionMatrix matrix, BufferedReader networkReader) throws ParsingException
	{
		// Read in the graph
		Graph<String,String> graph = null;
		try
		{
			graph = SIFReader.read(networkReader);
		}
		catch (IOException e)
		{
			throw new ParsingException("Network", e.getMessage());
		}
		
		// Create a map from a gene name to a matrix index
		final Map<String,Integer> geneNameToIndexMap = new HashMap<String,Integer>(matrix.genes.length);
		for (int i = 0; i < matrix.genes.length; i++)
			geneNameToIndexMap.put(matrix.genes[i], i);

		// Convert the graph where the node object is a String to an Activity
		TypeConverter<String,String,Activity,String> typeConverter
			= new TypeConverter<String,String,Activity,String>()
		{
			public Activity convertNodeObject(String nodeName)
			{
				Activity activity = new Activity();
				activity.name = nodeName;
				Integer index = geneNameToIndexMap.get(nodeName);
				activity.matrixIndex = (index == null ? -1 : index);
				return activity;
			}

			public String convertEdgeObject(String edge)
			{
				return edge;
			}
		};

		return new LinkedListGraph<Activity,String>(graph, typeConverter);
	}
}
