package SawdServer;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import oiler.Graph;
import oiler.LinkedListGraph;
import oiler.util.IntIterator;

/**
 * A container for all the graphs stored on the server
 **/
class Graphs implements Serializable
{
	List<SawdGraph> graphs = new ArrayList<SawdGraph>();
	Map<String,String> globalAttrs = new TreeMap<String,String>();

	public synchronized String[] get_global_attribute_names()
	{
		Set<String> keys = globalAttrs.keySet();
		String[] result = new String[keys.size()];
		int i = 0;
		for (String key : keys)
			result[i++] = key;
		return result;
	}

	public synchronized String get_global_attribute(String attr_name)
	{
		return globalAttrs.get(attr_name);
	}

	public synchronized String set_global_attribute(String attr_name, String attr_value)
	{
		return globalAttrs.put(attr_name, attr_value);
	}

	public synchronized int[] list_graphs()
	{
		int size = 0;
		for (int i = 0; i < graphs.size(); i++)
			if (graphs.get(i) != null)
				size++;

		int[] result = new int[size];
		int result_i = 0;
		for (int i = 0; i < graphs.size(); i++)
			if (graphs.get(i) != null)
				result[result_i++] = i;
		return result;
	}

	public synchronized int new_graph()
	{
		int result = graphs.size();
		graphs.add(new SawdGraph());
		return result;
	}

	public synchronized void delete_graph(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		
		graphs.set(graph_index, null);
	}

	public synchronized String[] get_graph_attribute_names(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		Set<String> keys = graphs.get(graph_index).attrs.keySet();
		String[] result = new String[keys.size()];
		int i = 0;
		for (String key : keys)
			result[i++] = key;
		return result;
	}

	public synchronized String get_graph_attribute(int graph_index, String attr_name) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		return graphs.get(graph_index).attrs.get(attr_name);
	}

	public synchronized String set_graph_attribute(int graph_index, String attr_name, String attr_value) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		return graphs.get(graph_index).attrs.put(attr_name, attr_value);
	}

	public synchronized int[] list_of_nodes(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		return intIteratorToArray(graphs.get(graph_index).nodes());
	}

	public synchronized int new_node(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		return graphs.get(graph_index).addNode(new TreeMap<String,String>());
	}

	public synchronized void delete_node(int graph_index, int node_index) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(node_index))
			throw new InvalidNodeIndexException(node_index);

		graph.removeNode(node_index);
	}

	public synchronized String[] get_node_attribute_names(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		
		Set<String> names = graphs.get(graph_index).nodeAttrs;
		String[] result = new String[names.size()];
		int i = 0;
		for (String name : names)
			result[i++] = name;
		return result;
	}

	public synchronized String get_node_attribute(int graph_index, int node_index, String attr_name) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(node_index))
			throw new InvalidNodeIndexException(node_index);
		
		return graph.nodeObject(node_index).get(attr_name);
	}

	public synchronized void set_node_attribute(int graph_index, int node_index, String attr_name, String attr_value) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(node_index))
			throw new InvalidNodeIndexException(node_index);
		
		if (!graph.nodeAttrs.contains(attr_name))
			graph.nodeAttrs.add(attr_name);

		graph.nodeObject(node_index).put(attr_name, attr_value);
	}

	public synchronized int[] get_adjcacent_nodes(int graph_index, int node_index) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(node_index))
			throw new InvalidNodeIndexException(node_index);
		
		IntIterator adj_nodes = graph.adjacentNodes(node_index, Graph.UNDIRECTED_EDGE);
		return intIteratorToArray(adj_nodes);
	}

	public synchronized int[] get_adjcacent_edges(int graph_index, int node_index) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(node_index))
			throw new InvalidNodeIndexException(node_index);
		
		IntIterator adj_edges = graph.adjacentEdges(node_index, Graph.UNDIRECTED_EDGE);
		return intIteratorToArray(adj_edges);
	}

	public synchronized int[] list_of_edges(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		return intIteratorToArray(graphs.get(graph_index).edges());
	}

	public synchronized int new_edge(int graph_index, int source_index, int target_index) throws InvalidGraphIndexException, InvalidNodeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.nodeExists(source_index))
			throw new InvalidNodeIndexException(source_index);
		if (!graph.nodeExists(target_index))
			throw new InvalidNodeIndexException(target_index);
		
		return graph.addEdge(source_index, target_index, new TreeMap<String,String>(), Graph.UNDIRECTED_EDGE);
	}

	public synchronized void delete_edge(int graph_index, int edge_index) throws InvalidGraphIndexException, InvalidEdgeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.edgeExists(edge_index))
			throw new InvalidEdgeIndexException(edge_index);

		graph.removeEdge(edge_index);
	}

	public synchronized String[] get_edge_attribute_names(int graph_index) throws InvalidGraphIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size() || graphs.get(graph_index) == null)
			throw new InvalidGraphIndexException(graph_index);
		
		Set<String> names = graphs.get(graph_index).edgeAttrs;
		String[] result = new String[names.size()];
		int i = 0;
		for (String name : names)
			result[i++] = name;
		return result;
	}

	public synchronized String get_edge_attribute(int graph_index, int edge_index, String attr_name) throws InvalidGraphIndexException, InvalidEdgeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.edgeExists(edge_index))
			throw new InvalidEdgeIndexException(edge_index);
		
		return graph.edgeObject(edge_index).get(attr_name);
	}

	public synchronized void set_edge_attribute(int graph_index, int edge_index, String attr_name, String attr_value) throws InvalidGraphIndexException, InvalidEdgeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.edgeExists(edge_index))
			throw new InvalidEdgeIndexException(edge_index);
		
		if (!graph.edgeAttrs.contains(attr_name))
			graph.edgeAttrs.add(attr_name);

		graph.edgeObject(edge_index).put(attr_name, attr_value);
	}

	public synchronized int get_edge_source(int graph_index, int edge_index) throws InvalidGraphIndexException, InvalidEdgeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.edgeExists(edge_index))
			throw new InvalidEdgeIndexException(edge_index);

		return graph.edgeSource(edge_index);
	}

	public synchronized int get_edge_target(int graph_index, int edge_index) throws InvalidGraphIndexException, InvalidEdgeIndexException
	{
		if (graph_index < 0 || graph_index >= graphs.size())
			throw new InvalidGraphIndexException(graph_index);

		SawdGraph graph = graphs.get(graph_index);
		if (graph == null)
			throw new InvalidGraphIndexException(graph_index);
		
		if (!graph.edgeExists(edge_index))
			throw new InvalidEdgeIndexException(edge_index);

		return graph.edgeTarget(edge_index);
	}
	
	private synchronized int[] intIteratorToArray(IntIterator iterator)
	{
		int[] result = new int[iterator.numRemaining()];
		int result_i = 0;
		while (iterator.hasNext())
			result[result_i++] = iterator.next();
		return result;
	}
}

class SawdGraph extends LinkedListGraph<Map<String,String>,Map<String,String>> implements Serializable
{
	public Map<String,String> attrs = new TreeMap<String,String>();
	public Set<String> nodeAttrs = new TreeSet<String>();
	public Set<String> edgeAttrs = new TreeSet<String>();

	public SawdGraph()
	{
		super();
	}
}

class InvalidGraphIndexException extends Exception
{
	public InvalidGraphIndexException(int index)
	{
		super(Integer.toString(index));
	}
}

class InvalidNodeIndexException extends Exception
{
	public InvalidNodeIndexException(int index)
	{
		super(Integer.toString(index));
	}
}

class InvalidEdgeIndexException extends Exception
{
	public InvalidEdgeIndexException(int index)
	{
		super(Integer.toString(index));
	}
}
