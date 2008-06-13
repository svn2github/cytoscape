package SawdServer;

import org.junit.*;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;

import java.io.*;
import java.net.*;

public class SawdServerTest
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(SawdServerTest.class);
	}

	static String PARAM_SEPARATOR = Servicer.PARAM_SEPARATOR;

	Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;

	private String[] read_strings() throws IOException
	{
		String line = in.readLine();
		if (line.length() == 0)
			return new String[0];
		else
			return line.split(PARAM_SEPARATOR);
	}

	private int[] read_ints() throws IOException
	{
		String[] strings = read_strings();
		int[] ints = new int[strings.length];
		for (int i = 0; i < ints.length; i++)
			ints[i] = Integer.parseInt(strings[i]);
		return ints;
	}

	private void write(String ... args)
	{
		for (int i = 0; i < args.length; i++)
		{
			out.print(args[i]);
			if (i != args.length - 1)
				out.print(PARAM_SEPARATOR);
		}
		out.println();
	}

	private boolean array_contains(String[] array, String value)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(value))
				return true;
		return false;
	}

	@Before
	public void startClient() throws IOException, UnknownHostException
	{
		socket = new Socket("localhost", 2626);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	@After
	public void stopClient() throws IOException
	{
		in.close();
		out.close();
		socket.close();
	}

	@Test
	public void testEmptyServer() throws IOException
	{
		out.println("list_graphs");
		assertTrue(read_ints().length == 0);
	}

	@Test
	public void testNewDeleteGraph() throws IOException
	{
		String result;
		int[] graph_ids;

		// test creating graph 0
		out.println("new_graph");
		int graph_id0 = Integer.parseInt(in.readLine());

		out.println("list_graphs");
		graph_ids = read_ints();
		assertTrue(graph_ids.length == 1);
		assertTrue(graph_ids[0] == graph_id0);
		// test creating graph 1
		out.println("new_graph");
		int graph_id1 = Integer.parseInt(in.readLine());

		out.println("list_graphs");
		graph_ids = read_ints();
		assertTrue(graph_ids.length == 2);
		assertTrue(graph_ids[0] == graph_id0);
		assertTrue(graph_ids[1] == graph_id1);

		// test creating graph 2
		out.println("new_graph");
		int graph_id2 = Integer.parseInt(in.readLine());

		out.println("list_graphs");
		graph_ids = read_ints();
		assertTrue(graph_ids.length == 3);
		assertTrue(graph_ids[0] == graph_id0);
		assertTrue(graph_ids[1] == graph_id1);
		assertTrue(graph_ids[2] == graph_id2);

		// test deleting non-existent graph
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, 25123));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting graph 1
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, graph_id1));
		result = in.readLine();
		assertTrue(result.length() == 0);

		out.println("list_graphs");
		graph_ids = read_ints();
		assertTrue(graph_ids.length == 2);
		assertTrue(graph_ids[0] == graph_id0);
		assertTrue(graph_ids[1] == graph_id2);

		// test deleting graph 1 again
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, graph_id1));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting graph 0 and 2
		out.println(String.format("delete_graph%s%d%s%d", PARAM_SEPARATOR, graph_id0, PARAM_SEPARATOR, graph_id2));
		result = in.readLine();
		assertTrue(result.length() == 0);

		out.println("list_graphs");
		graph_ids = read_ints();
		assertTrue(graph_ids.length == 0);

		// test deleting graph 0 again
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, graph_id0));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting graph 2 again
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, graph_id0));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting graph 0 and 2 again
		out.println(String.format("delete_graph%s%d%s%d", PARAM_SEPARATOR, graph_id0, PARAM_SEPARATOR, graph_id2));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting with too few parameters
		out.println(String.format("delete_graph"));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		out.println(String.format("delete_graph%s", PARAM_SEPARATOR));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));

		// test deleting with an invalid graph index
		out.println(String.format("delete_graph%sa", PARAM_SEPARATOR));
		result = in.readLine();
		assertTrue(result.startsWith("ERROR"));
	}

	@Test
	public void testGraphAttributes() throws IOException
	{
		// create a graph
		out.println("new_graph");
		int graph_id = Integer.parseInt(in.readLine());

		// ensure there are no attributes already
		out.println(String.format("get_graph_attribute_names%s%d", PARAM_SEPARATOR, graph_id));
		assertTrue(in.readLine().length() == 0);

		// add an attribute
		out.println(String.format("set_graph_attribute%s%d%sname%stest", PARAM_SEPARATOR, graph_id, PARAM_SEPARATOR, PARAM_SEPARATOR));
		assertTrue(in.readLine().length() == 0);

		// add another attribute
		out.println(String.format("set_graph_attribute%s%d%sscore%s1.0", PARAM_SEPARATOR, graph_id, PARAM_SEPARATOR, PARAM_SEPARATOR));
		assertTrue(in.readLine().length() == 0);

		// see if it's in list of attribute names
		out.println(String.format("get_graph_attribute_names%s%d", PARAM_SEPARATOR, graph_id));
		String[] attrs = read_strings();
		assertTrue(attrs.length == 2);
		assertTrue(array_contains(attrs, "name"));
		assertTrue(array_contains(attrs, "score"));

		// test if get returns the same value
		out.println(String.format("get_graph_attribute%s%d%sname", PARAM_SEPARATOR, graph_id, PARAM_SEPARATOR));
		assertTrue(in.readLine().equals("test"));
		out.println(String.format("get_graph_attribute%s%d%sscore", PARAM_SEPARATOR, graph_id, PARAM_SEPARATOR));
		assertTrue(in.readLine().equals("1.0"));

		// delete the graph
		out.println(String.format("delete_graph%s%d", PARAM_SEPARATOR, graph_id));
		assertTrue(in.readLine().length() == 0);
	}

	@Test
	public void testGraphAttributesFail() throws IOException
	{
		// test for invalid index
		out.println(String.format("get_graph_attribute_names%s%d", PARAM_SEPARATOR, 272123));
		assertTrue(in.readLine().startsWith("ERROR"));

		// test for non-numerical index
		out.println(String.format("get_graph_attribute_names%sa", PARAM_SEPARATOR));
		assertTrue(in.readLine().startsWith("ERROR"));

		// test for invalid index
		out.println(String.format("set_graph_attribute%s%d%sname%stest", PARAM_SEPARATOR, 272123, PARAM_SEPARATOR, PARAM_SEPARATOR));
		assertTrue(in.readLine().startsWith("ERROR"));

		// test for non-numerical index
		out.println(String.format("set_graph_attribute%sa%sname%stest", PARAM_SEPARATOR, PARAM_SEPARATOR, PARAM_SEPARATOR));
		assertTrue(in.readLine().startsWith("ERROR"));

		// test for invalid index
		out.println(String.format("get_graph_attribute_names%s%d", PARAM_SEPARATOR, 272123));
		assertTrue(in.readLine().startsWith("ERROR"));

		// test for non-numerical index
		out.println(String.format("get_graph_attribute_names%sa", PARAM_SEPARATOR));
		assertTrue(in.readLine().startsWith("ERROR"));
	}

	@Test
	public void testNewAndDeleteNodes() throws IOException
	{
		String[] node_indices;

		// create a graph
		out.println("new_graph");
		String graph_id = in.readLine();
		assertFalse(graph_id.startsWith("ERROR"));

		// ensure there are no nodes for the graph
		write("list_nodes", graph_id);
		assertTrue(read_ints().length == 0);

		// create a node
		write("new_node", graph_id);
		String node_id0 = in.readLine();
		assertFalse(node_id0.startsWith("ERROR"));

		// create another node
		write("new_node", graph_id);
		String node_id1 = in.readLine();
		assertFalse(node_id1.startsWith("ERROR"));

		// ensure the nodes are listed
		write("list_nodes", graph_id);
		node_indices = read_strings();
		assertTrue(node_indices.length == 2);
		assertTrue(array_contains(node_indices, node_id0));
		assertTrue(array_contains(node_indices, node_id1));

		// delete a node
		write("delete_node", graph_id, node_id0);
		assertFalse(in.readLine().startsWith("ERROR"));

		// ensure it's not listed
		write("list_nodes", graph_id);
		node_indices = read_strings();
		assertTrue(node_indices.length == 1);
		assertTrue(array_contains(node_indices, node_id1));

		// delete the node again
		write("delete_node", graph_id, node_id0);
		assertTrue(in.readLine().startsWith("ERROR"));

		// delete a node
		write("delete_node", graph_id, node_id1);
		assertFalse(in.readLine().startsWith("ERROR"));

		// ensure it's not listed
		write("list_nodes", graph_id);
		node_indices = read_strings();
		assertTrue(node_indices.length == 0);

		// delete the node again
		write("delete_node", graph_id, node_id1);
		assertTrue(in.readLine().startsWith("ERROR"));

		// delete the graph
		write("delete_graph", graph_id);
		assertFalse(in.readLine().startsWith("ERROR"));
	}


	@Test
	public void testNodeAttributes() throws IOException
	{
		String[] names;

		// create a graph
		out.println("new_graph");
		String graph_id = in.readLine();
		assertFalse(graph_id.startsWith("ERROR"));

		// create a node
		write("new_node", graph_id);
		String node_id0 = in.readLine();
		assertFalse(node_id0.startsWith("ERROR"));

		// create another node
		write("new_node", graph_id);
		String node_id1 = in.readLine();
		assertFalse(node_id1.startsWith("ERROR"));

		// ensure no attributes have been set
		write("get_node_attribute_names", graph_id);
		names = read_strings();
		assertTrue(names.length == 0);

		// set some attributes
		write("set_node_attribute", graph_id, node_id0, "name", "first");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_node_attribute", graph_id, node_id0, "x", "-1.0");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_node_attribute", graph_id, node_id1, "name", "second");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_node_attribute", graph_id, node_id1, "y", "1.0");
		assertFalse(in.readLine().startsWith("ERROR"));

		// get the attributes
		write("get_node_attribute", graph_id, node_id0, "name");
		assertTrue(in.readLine().equals("first"));
		write("get_node_attribute", graph_id, node_id0, "x");
		assertTrue(in.readLine().equals("-1.0"));
		write("get_node_attribute", graph_id, node_id1, "name");
		assertTrue(in.readLine().equals("second"));
		write("get_node_attribute", graph_id, node_id1, "y");
		assertTrue(in.readLine().equals("1.0"));

		// ensure the attributes are listed
		write("get_node_attribute_names", graph_id);
		names = read_strings();
		assertTrue(names.length == 3);
		assertTrue(array_contains(names, "name"));
		assertTrue(array_contains(names, "x"));
		assertTrue(array_contains(names, "y"));

		// delete the graph
		write("delete_graph", graph_id);
		assertFalse(in.readLine().startsWith("ERROR"));
	}

	@Test
	public void testNewAndDeleteEdges() throws IOException
	{
		String[] indices;

		// create a graph
		out.println("new_graph");
		String graph_id = in.readLine();
		assertFalse(graph_id.startsWith("ERROR"));

		// create a node
		write("new_node", graph_id);
		String node_id0 = in.readLine();
		assertFalse(node_id0.startsWith("ERROR"));

		// create another node
		write("new_node", graph_id);
		String node_id1 = in.readLine();
		assertFalse(node_id1.startsWith("ERROR"));

		// ensure there are no edges
		write("list_edges", graph_id);
		assertTrue(read_strings().length == 0);

		// create yet another node
		write("new_node", graph_id);
		String node_id2 = in.readLine();
		assertFalse(node_id2.startsWith("ERROR"));

		// create an edge
		write("new_edge", graph_id, node_id0, node_id1);
		String edge_id0 = in.readLine();
		assertFalse(edge_id0.startsWith("ERROR"));

		// create another edge
		write("new_edge", graph_id, node_id1, node_id2);
		String edge_id1 = in.readLine();
		assertFalse(edge_id1.startsWith("ERROR"));

		// ensure the edges are listed
		write("list_edges", graph_id);
		indices = read_strings();
		assertTrue(indices.length == 2);
		assertTrue(array_contains(indices, edge_id0));
		assertTrue(array_contains(indices, edge_id1));

		// delete an edge
		write("delete_edge", graph_id, edge_id0);
		assertFalse(in.readLine().startsWith("ERROR"));

		// ensure it's no longer listed
		write("list_edges", graph_id);
		indices = read_strings();
		assertTrue(indices.length == 1);
		assertTrue(array_contains(indices, edge_id1));

		// delete the edge again
		write("delete_edge", graph_id, edge_id0);
		assertTrue(in.readLine().startsWith("ERROR"));

		// delete an edge
		write("delete_edge", graph_id, edge_id1);
		assertFalse(in.readLine().startsWith("ERROR"));

		// ensure it's no longer listed
		write("list_edges", graph_id);
		indices = read_strings();
		assertTrue(indices.length == 0);

		// delete the edge again
		write("delete_edge", graph_id, edge_id1);
		assertTrue(in.readLine().startsWith("ERROR"));

		// delete the graph
		write("delete_graph", graph_id);
		assertFalse(in.readLine().startsWith("ERROR"));
	}

	@Test
	public void testEdgeAttributes() throws IOException
	{
		String[] names;

		// create a graph
		out.println("new_graph");
		String graph_id = in.readLine();
		assertFalse(graph_id.startsWith("ERROR"));

		// create a node
		write("new_node", graph_id);
		String node_id0 = in.readLine();
		assertFalse(node_id0.startsWith("ERROR"));

		// create another node
		write("new_node", graph_id);
		String node_id1 = in.readLine();
		assertFalse(node_id1.startsWith("ERROR"));

		// create yet another node
		write("new_node", graph_id);
		String node_id2 = in.readLine();
		assertFalse(node_id2.startsWith("ERROR"));

		// create an edge
		write("new_edge", graph_id, node_id0, node_id1);
		String edge_id0 = in.readLine();
		assertFalse(edge_id0.startsWith("ERROR"));

		// create another edge
		write("new_edge", graph_id, node_id1, node_id2);
		String edge_id1 = in.readLine();
		assertFalse(edge_id1.startsWith("ERROR"));

		// ensure no attributes have been set
		write("get_edge_attribute_names", graph_id);
		names = read_strings();
		assertTrue(names.length == 0);

		// set some attributes
		write("set_edge_attribute", graph_id, edge_id0, "name", "first");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_edge_attribute", graph_id, edge_id0, "x", "-1.0");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_edge_attribute", graph_id, edge_id1, "name", "second");
		assertFalse(in.readLine().startsWith("ERROR"));
		write("set_edge_attribute", graph_id, edge_id1, "y", "1.0");
		assertFalse(in.readLine().startsWith("ERROR"));

		// get the attributes
		write("get_edge_attribute", graph_id, edge_id0, "name");
		assertTrue(in.readLine().equals("first"));
		write("get_edge_attribute", graph_id, edge_id0, "x");
		assertTrue(in.readLine().equals("-1.0"));
		write("get_edge_attribute", graph_id, edge_id1, "name");
		assertTrue(in.readLine().equals("second"));
		write("get_edge_attribute", graph_id, edge_id1, "y");
		assertTrue(in.readLine().equals("1.0"));

		// ensure the attributes are listed
		write("get_edge_attribute_names", graph_id);
		names = read_strings();
		assertTrue(names.length == 3);
		assertTrue(array_contains(names, "name"));
		assertTrue(array_contains(names, "x"));
		assertTrue(array_contains(names, "y"));

		// delete the graph
		write("delete_graph", graph_id);
		assertFalse(in.readLine().startsWith("ERROR"));
	}

	@Test
	public void testTopology() throws IOException
	{
		String[] names;

		// create a graph
		out.println("new_graph");
		String graph_id = in.readLine();
		assertFalse(graph_id.startsWith("ERROR"));

		// create a node
		write("new_node", graph_id);
		String node_id0 = in.readLine();
		assertFalse(node_id0.startsWith("ERROR"));

		// create another node
		write("new_node", graph_id);
		String node_id1 = in.readLine();
		assertFalse(node_id1.startsWith("ERROR"));

		// create yet another node
		write("new_node", graph_id);
		String node_id2 = in.readLine();
		assertFalse(node_id2.startsWith("ERROR"));

		// create an edge
		write("new_edge", graph_id, node_id0, node_id1);
		String edge_id0 = in.readLine();
		assertFalse(edge_id0.startsWith("ERROR"));

		// create another edge
		write("new_edge", graph_id, node_id1, node_id2);
		String edge_id1 = in.readLine();
		assertFalse(edge_id1.startsWith("ERROR"));

		// check sources and targets of edge 0
		write("get_edge_source", graph_id, edge_id0);
		assertTrue(in.readLine().equals(node_id0));
		write("get_edge_target", graph_id, edge_id0);
		assertTrue(in.readLine().equals(node_id1));

		// check sources and targets of edge 1
		write("get_edge_source", graph_id, edge_id1);
		assertTrue(in.readLine().equals(node_id1));
		write("get_edge_target", graph_id, edge_id1);
		assertTrue(in.readLine().equals(node_id2));

		// check adjacent nodes of node 0
		write("get_adjacent_nodes", graph_id, node_id0);
		assertTrue(in.readLine().equals(node_id1));

		// check adjacent nodes of node 1
		write("get_adjacent_nodes", graph_id, node_id1);
		names = read_strings();
		assertTrue(names.length == 2);
		assertTrue(array_contains(names, node_id0));
		assertTrue(array_contains(names, node_id2));

		// check adjacent nodes of node 2
		write("get_adjacent_nodes", graph_id, node_id2);
		assertTrue(in.readLine().equals(node_id1));

		// check incident edges of node 0
		write("get_incident_edges", graph_id, node_id0);
		assertTrue(in.readLine().equals(edge_id0));

		// check incident edges of node 1
		write("get_incident_edges", graph_id, node_id1);
		names = read_strings();
		assertTrue(names.length == 2);
		assertTrue(array_contains(names, edge_id0));
		assertTrue(array_contains(names, edge_id1));

		// check incident edges of node 2
		write("get_incident_edges", graph_id, node_id2);
		assertTrue(in.readLine().equals(edge_id1));

		// delete the graph
		write("delete_graph", graph_id);
		assertFalse(in.readLine().startsWith("ERROR"));
	}
}
