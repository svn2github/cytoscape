package SawdServer;

import java.util.Arrays;
import java.util.List;

import java.net.Socket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Services a client's requests
 **/
class Servicer implements Runnable
{
	Socket socket;
	Graphs graphs;
	BufferedReader reader = null;
	PrintWriter writer = null;
	boolean processingRequests = true;
	String clientAndPort = "unknown";
	boolean hasLock = false;

	public static String PARAM_SEPARATOR = ",";

	public Servicer(Socket socket, Graphs graphs)
	{
		this.socket = socket;
		this.graphs = graphs;

		try
		{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
		}
		catch (IOException e)
		{
			SawdServer.log("Failed start I/O with client: " + e.getMessage());
		}

		clientAndPort = String.format("%s:%s", socket.getInetAddress().toString(), socket.getPort());
	}

	public void run()
	{
		if (reader == null || writer == null) return;
		SawdServer.log("Started communication with client " + clientAndPort);

		try
		{
			String line = null;
			while (processingRequests && ((line = reader.readLine()) != null))
			{
				// ignore empty lines
				if (line.length() == 0)
					continue;

				String[] pieces = line.split(PARAM_SEPARATOR);
				/*
				for (int i = 0; i < pieces.length; i++)
				{
					System.out.print(pieces[i]);
					System.out.print(' ');
				}
				System.out.println();
				*/
				String command = pieces[0].toLowerCase();


				if (command.equals("get_global_attribute_names"))
					handle_get_global_attribute_names(pieces);
				else if (command.equals("get_global_attribute"))
					handle_get_global_attribute(pieces);
				else if (command.equals("set_global_attribute"))
					handle_set_global_attribute(pieces);
				else if (command.equals("list_graphs"))
					handle_list_graphs(pieces);
				else if (command.equals("new_graph"))
					handle_new_graph(pieces);
				else if (command.equals("delete_graph"))
					handle_delete_graph(pieces);
				else if (command.equals("get_graph_attribute_names"))
					handle_get_graph_attribute_names(pieces);
				else if (command.equals("get_graph_attribute"))
					handle_get_graph_attribute(pieces);
				else if (command.equals("set_graph_attribute"))
					handle_set_graph_attribute(pieces);
				else if (command.equals("list_nodes"))
					handle_list_nodes(pieces);
				else if (command.equals("new_node"))
					handle_new_node(pieces);
				else if (command.equals("delete_node"))
					handle_delete_node(pieces);
				else if (command.equals("get_node_attribute_names"))
					handle_get_node_attribute_names(pieces);
				else if (command.equals("get_node_attribute"))
					handle_get_node_attribute(pieces);
				else if (command.equals("set_node_attribute"))
					handle_set_node_attribute(pieces);
				else if (command.equals("get_adjacent_nodes"))
					handle_get_adjacent_nodes(pieces);
				else if (command.equals("get_incident_edges"))
					handle_get_incident_edges(pieces);
				else if (command.equals("list_edges"))
					handle_list_edges(pieces);
				else if (command.equals("new_edge"))
					handle_new_edge(pieces);
				else if (command.equals("delete_edge"))
					handle_delete_edge(pieces);
				else if (command.equals("get_edge_attribute_names"))
					handle_get_edge_attribute_names(pieces);
				else if (command.equals("get_edge_attribute"))
					handle_get_edge_attribute(pieces);
				else if (command.equals("set_edge_attribute"))
					handle_set_edge_attribute(pieces);
				else if (command.equals("get_edge_source"))
					handle_get_edge_source(pieces);
				else if (command.equals("get_edge_target"))
					handle_get_edge_target(pieces);
				else if (command.equals("lock"))
					handle_lock(pieces);
				else if (command.equals("unlock"))
					handle_unlock(pieces);
				else
					issue_error("command not understood");
			}

			reader.close();
			writer.close();
			socket.close();

		}
		catch (IOException e)
		{
			SawdServer.log("I/O error with client " + clientAndPort + " because: " + e.getMessage());
		}

		SawdServer.log("Ended communication with client at " + clientAndPort);
	}

	public void stop()
	{
		processingRequests = false;
	}

	private void issue_error(String message)
	{
		writer.println("ERROR:" + message);
	}

	private void handle_get_global_attribute_names(String[] args)
	{
		String[] attr_names = graphs.get_global_attribute_names();
		for (int i = 0; i < attr_names.length; i++)
		{
			writer.print(attr_names[i]);
			if (i != attr_names.length - 1)
				writer.print(PARAM_SEPARATOR);
		}
		writer.println();
	}

	private void handle_get_global_attribute(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		String attr_name = args[1];
		String attr_value = graphs.get_global_attribute(attr_name);
		if (attr_value == null)
			writer.println();
		else
			writer.println(attr_value);
	}

	private void handle_set_global_attribute(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		String attr_name = args[1];
		String attr_value = args[2];
		graphs.set_global_attribute(attr_name, attr_value);
		writer.println();
	}
	private void handle_list_graphs(String[] args)
	{
		int[] graph_indices = graphs.list_graphs();
		for (int i = 0; i < graph_indices.length; i++)
		{
			writer.print(graph_indices[i]);
			if (i != graph_indices.length - 1)
				writer.print(PARAM_SEPARATOR);
		}
		writer.println();
	}

	private void handle_new_graph(String[] args)
	{
		writer.println(graphs.new_graph());
	}

	private void handle_delete_graph(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		for (int i = 1; i < args.length; i++)
		{
			int index = -1;
			try
			{
				index = Integer.parseInt(args[i]);
			}
			catch (NumberFormatException e)
			{
				issue_error("graph index \'" + args[i] + "\' is not a number");
				return;
			}

			try
			{
				graphs.delete_graph(index);
			}
			catch (InvalidGraphIndexException e)
			{
				issue_error("graph index \'" + args[i] + "\' is invalid");
				return;
			}
		}
		writer.println();
	}

	private void handle_get_graph_attribute_names(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			String[] attr_names = graphs.get_graph_attribute_names(index);
			for (int i = 0; i < attr_names.length; i++)
			{
				writer.print(attr_names[i]);
				if (i != attr_names.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
	}

	private void handle_get_graph_attribute(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}
		String attr_name = args[2];

		try
		{
			String attr_value = graphs.get_graph_attribute(index, attr_name);
			if (attr_value == null)
				writer.println();
			else
				writer.println(attr_value);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
	}

	private void handle_set_graph_attribute(String[] args)
	{
		if (args.length <= 3)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}
		String attr_name = args[2];
		String attr_value = args[3];

		try
		{
			graphs.set_graph_attribute(index, attr_name, attr_value);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}

		writer.println();
	}

	private void handle_list_nodes(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			int[] node_indices = graphs.list_of_nodes(index);
			for (int i = 0; i < node_indices.length; i++)
			{
				writer.print(node_indices[i]);
				if (i != node_indices.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
	}

	private void handle_new_node(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			int node_index = graphs.new_node(index);
			writer.println(node_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
	}

	private void handle_delete_node(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index = -1;
		try
		{
			node_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			graphs.delete_node(graph_index, node_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + args[2] + "\' is invalid");
			return;
		}

		writer.println();
	}

	private void handle_get_node_attribute_names(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			String[] attr_names = graphs.get_node_attribute_names(index);
			for (int i = 0; i < attr_names.length; i++)
			{
				writer.print(attr_names[i]);
				if (i != attr_names.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
	}

	private void handle_get_node_attribute(String[] args)
	{
		if (args.length <= 3)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index = -1;
		try
		{
			node_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		String attr_name = args[3];

		try
		{
			String attr_value = graphs.get_node_attribute(graph_index, node_index, attr_name);
			if (attr_value == null)
				writer.println();
			else
				writer.println(attr_value);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_set_node_attribute(String[] args)
	{
		if (args.length <= 4)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index = -1;
		try
		{
			node_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		String attr_name = args[3];
		String attr_value = args[4];

		try
		{
			graphs.set_node_attribute(graph_index, node_index, attr_name, attr_value);
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_get_adjacent_nodes(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index = -1;
		try
		{
			node_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			int[] nodes = graphs.get_adjcacent_nodes(graph_index, node_index);
			for (int i = 0; i < nodes.length; i++)
			{
				writer.print(nodes[i]);
				if (i != nodes.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_get_incident_edges(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index = -1;
		try
		{
			node_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			int[] edges = graphs.get_adjcacent_edges(graph_index, node_index);
			for (int i = 0; i < edges.length; i++)
			{
				writer.print(edges[i]);
				if (i != edges.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_list_edges(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			int[] node_indices = graphs.list_of_edges(index);
			for (int i = 0; i < node_indices.length; i++)
			{
				writer.print(node_indices[i]);
				if (i != node_indices.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
	}

	private void handle_new_edge(String[] args)
	{
		if (args.length <= 3)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int node_index0 = -1;
		try
		{
			node_index0 = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[2] + "\' is not a number");
			return;
		}

		int node_index1 = -1;
		try
		{
			node_index1 = Integer.parseInt(args[3]);
		}
		catch (NumberFormatException e)
		{
			issue_error("node index \'" + args[3] + "\' is not a number");
			return;
		}

		try
		{
			int edge_index = graphs.new_edge(graph_index, node_index0, node_index1);
			writer.println(edge_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
		catch (InvalidNodeIndexException e)
		{
			issue_error("node index \'" + e.getMessage() + "\' is invalid");
			return;
		}
	}

	private void handle_delete_edge(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int edge_index = -1;
		try
		{
			edge_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("edge index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			graphs.delete_edge(graph_index, edge_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
			return;
		}
		catch (InvalidEdgeIndexException e)
		{
			issue_error("edge index \'" + args[2] + "\' is invalid");
			return;
		}

		writer.println();
	}

	private void handle_get_edge_attribute_names(String[] args)
	{
		if (args.length <= 1)
		{
			issue_error("not enough arguments");
			return;
		}

		int index = -1;
		try
		{
			index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		try
		{
			String[] attr_names = graphs.get_edge_attribute_names(index);
			for (int i = 0; i < attr_names.length; i++)
			{
				writer.print(attr_names[i]);
				if (i != attr_names.length - 1)
					writer.print(PARAM_SEPARATOR);
			}
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
	}

	private void handle_get_edge_attribute(String[] args)
	{
		if (args.length <= 3)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int edge_index = -1;
		try
		{
			edge_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("edge index \'" + args[2] + "\' is not a number");
			return;
		}

		String attr_name = args[3];

		try
		{
			String attr_value = graphs.get_edge_attribute(graph_index, edge_index, attr_name);
			if (attr_value == null)
				writer.println();
			else
				writer.println(attr_value);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidEdgeIndexException e)
		{
			issue_error("edge index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_set_edge_attribute(String[] args)
	{
		if (args.length <= 4)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int edge_index = -1;
		try
		{
			edge_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("edge index \'" + args[2] + "\' is not a number");
			return;
		}

		String attr_name = args[3];
		String attr_value = args[4];

		try
		{
			graphs.set_edge_attribute(graph_index, edge_index, attr_name, attr_value);
			writer.println();
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidEdgeIndexException e)
		{
			issue_error("edge index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_get_edge_source(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int edge_index = -1;
		try
		{
			edge_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("edge index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			int node_index = graphs.get_edge_source(graph_index, edge_index);
			writer.println(node_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidEdgeIndexException e)
		{
			issue_error("edge index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_get_edge_target(String[] args)
	{
		if (args.length <= 2)
		{
			issue_error("not enough arguments");
			return;
		}

		int graph_index = -1;
		try
		{
			graph_index = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException e)
		{
			issue_error("graph index \'" + args[1] + "\' is not a number");
			return;
		}

		int edge_index = -1;
		try
		{
			edge_index = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException e)
		{
			issue_error("edge index \'" + args[2] + "\' is not a number");
			return;
		}

		try
		{
			int node_index = graphs.get_edge_target(graph_index, edge_index);
			writer.println(node_index);
		}
		catch (InvalidGraphIndexException e)
		{
			issue_error("graph index \'" + args[1] + "\' is invalid");
		}
		catch (InvalidEdgeIndexException e)
		{
			issue_error("edge index \'" + args[2] + "\' is invalid");
		}
	}

	private void handle_lock(String[] args)
	{
		//SawdServer.freeze(this);
		//hasLock = true;
		//writer.println();
	}

	private void handle_unlock(String[] args)
	{
		//SawdServer.thaw();
		//hasLock = false;
		//writer.println();
	}
}
