package SawdPinnacleZ;

import java.io.*;
import java.net.*;

public class SawdClient
{
	Socket socket;
	BufferedReader in = null;
	PrintWriter out = null;
	boolean isConnected = false;
	public static final String PARAM_SEPARATOR = ",";

	public SawdClient(String server, int port)
	{
		try
		{
			socket = new Socket(server, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			isConnected = true;
		}
		catch (UnknownHostException e)
		{
			report("Failed to connect to server: unknown host \'" + e.getMessage() + "\'");
		}
		catch (IOException e)
		{
			report("Failed to connect to server: " + e.getMessage());
		}
	}

	private void report(String message)
	{
		System.err.println(message);
	}

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

	private void write(String ... args) throws IOException
	{
		for (int i = 0; i < args.length; i++)
		{
			out.print(args[i]);
			if (i != args.length - 1)
				out.print(PARAM_SEPARATOR);
		}
		out.println();
	}


	public void close()
	{
		try
		{
			in.close();
			out.close();
			socket.close();
		}
		catch (IOException e)
		{
			report("Failed to close session: " + e.getMessage());
		}
		isConnected = false;
	}

	public boolean isConnected()
	{
		return isConnected;
	}

	public String[] get_global_attribute_names()
	{
		try
		{
			write("get_global_attribute_names");
			return read_strings();
		}
		catch (IOException e)
		{
			report("Failed to obtain global attribute names: " + e.getMessage());
			return null;
		}
	}

	public String get_global_attribute(String attr_name)
	{
		try
		{
			write("get_global_attribute", attr_name);
			String line = in.readLine();
			//System.out.println(attr_name + ": " + line);
			return line;
		}
		catch (IOException e)
		{
			report("Failed to obtain global attribute: " + e.getMessage());
			return null;
		}
	}

	public boolean set_global_attribute(String attr_name, String attr_value)
	{
		try
		{
			write("set_global_attribute", attr_name, attr_value);
			return !in.readLine().startsWith("ERROR");
		}
		catch (IOException e)
		{
			report("Failed to set global attribute: " + e.getMessage());
			return false;
		}
	}

	public int[] list_graphs()
	{
		try
		{
			out.println("list_graphs");
			return read_ints();
		}
		catch (IOException e)
		{
			report("Failed to get list of graphs: " + e.getMessage());
			return null;
		}
	}

	public int new_graph()
	{
		try
		{
			write("new_graph");
			return Integer.parseInt(in.readLine());
		}
		catch (IOException e)
		{
			report("Failed to create new graph: " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}

	public boolean delete_graph(int graph_index)
	{
		try
		{
			write("delete_graph", Integer.toString(graph_index));
			String line = in.readLine();
			return !line.startsWith("ERROR");
		}
		catch (IOException e)
		{
			report("Failed to delete graph: " + e.getMessage());
			return false;
		}
	}

	public String[] get_graph_attribute_names(int graph_index)
	{
		try
		{
			write("get_graph_attribute_names", Integer.toString(graph_index));
			return read_strings();
		}
		catch (IOException e)
		{
			report("Failed to obtain graph attribute names: " + e.getMessage());
			return null;
		}
	}

	public String get_graph_attribute(int graph_index, String attr_name)
	{
		try
		{
			write("get_graph_attribute", Integer.toString(graph_index), attr_name);
			return in.readLine();
		}
		catch (IOException e)
		{
			report("Failed to obtain graph attribute: " + e.getMessage());
			return null;
		}
	}

	public boolean set_graph_attribute(int graph_index, String attr_name, String attr_value)
	{
		try
		{
			write("set_graph_attribute", Integer.toString(graph_index), attr_name, attr_value);
			return !in.readLine().startsWith("ERROR");
		}
		catch (IOException e)
		{
			report("Failed to set graph attribute: " + e.getMessage());
			return false;
		}
	}

	public int[] list_nodes(int graph_index)
	{
		try
		{
			write("list_nodes", Integer.toString(graph_index));
			return read_ints();
		}
		catch (IOException e)
		{
			report("Failed to list nodes: " + e.getMessage());
			return null;
		}
	}

	public int new_node(int graph_index)
	{
		try
		{
			write("new_node", Integer.toString(graph_index));
			return Integer.parseInt(in.readLine());
		}
		catch (IOException e)
		{
			report("Failed to create new node: " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}

	public String[] get_node_attribute_names(int graph_index)
	{
		try
		{
			write("get_node_attribute_names", Integer.toString(graph_index));
			return read_strings();
		}
		catch (IOException e)
		{
			report("Failed to obtain node attribute names: " + e.getMessage());
			return null;
		}
	}

	public String get_node_attribute(int graph_index, int node_index, String attr_name)
	{
		try
		{
			write("get_node_attribute", Integer.toString(graph_index), Integer.toString(node_index), attr_name);
			return in.readLine();
		}
		catch (IOException e)
		{
			report("Failed to obtain node attribute: " + e.getMessage());
			return null;
		}

	}

	public boolean set_node_attribute(int graph_index, int node_index, String attr_name, String attr_value)
	{
		try
		{
			write("set_node_attribute", Integer.toString(graph_index), Integer.toString(node_index), attr_name, attr_value);
			return in.readLine().startsWith("ERROR");
		}
		catch (IOException e)
		{
			report("Failed to set node attribute: " + e.getMessage());
			return false;
		}
	}

	public int[] list_edges(int graph_index)
	{
		try
		{
			write("list_edges", Integer.toString(graph_index));
			return read_ints();
		}
		catch (IOException e)
		{
			report("Failed to list edges: " + e.getMessage());
			return null;
		}
	}

	public int new_edge(int graph_index, int source_index, int target_index)
	{
		try
		{
			write("new_edge", Integer.toString(graph_index), Integer.toString(source_index), Integer.toString(target_index));
			return Integer.parseInt(in.readLine());
		}
		catch (IOException e)
		{
			report("Failed to create new edge: " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}

	public String[] get_edge_attribute_names(int graph_index)
	{
		try
		{
			write("get_edge_attribute_names", Integer.toString(graph_index));
			return read_strings();
		}
		catch (IOException e)
		{
			report("Failed to obtain edge attribute names: " + e.getMessage());
			return null;
		}
	}

	public String get_edge_attribute(int graph_index, int edge_index, String attr_name)
	{
		try
		{
			write("get_edge_attribute", Integer.toString(graph_index), Integer.toString(edge_index), attr_name);
			return in.readLine();
		}
		catch (IOException e)
		{
			report("Failed to obtain edge attribute: " + e.getMessage());
			return null;
		}

	}

	public int get_edge_source(int graph_index, int edge_index)
	{
		try
		{
			write("get_edge_source", Integer.toString(graph_index), Integer.toString(edge_index));
			return Integer.parseInt(in.readLine());
		}
		catch (IOException e)
		{
			report("Failed to get edge source: " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}

	public int get_edge_target(int graph_index, int edge_index)
	{
		try
		{
			write("get_edge_target", Integer.toString(graph_index), Integer.toString(edge_index));
			return Integer.parseInt(in.readLine());
		}
		catch (IOException e)
		{
			report("Failed to get edge target: " + e.getMessage());
			return Integer.MIN_VALUE;
		}
	}
}
