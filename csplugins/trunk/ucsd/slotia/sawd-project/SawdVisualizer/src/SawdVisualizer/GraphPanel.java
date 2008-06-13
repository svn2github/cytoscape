package SawdVisualizer;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import org.jgraph.JGraph;
import org.jgraph.graph.*;
import org.jgraph.event.*;

public class GraphPanel extends JInternalFrame
{
	SawdClient client;
	int graph_index;

	GraphModel model;
	JGraph graph;

	DefaultTableModel graph_attributes;
	DefaultTableModel node_attributes;
	DefaultTableModel edge_attributes;

	JTable graph_table;
	JTable node_table;
	JTable edge_table;

	MyGraphSelectionListener myGraphSelectionListener;
	MyNodesListSelectionListener myNodesListSelectionListener;
	MyEdgesListSelectionListener myEdgesListSelectionListener;

	public GraphPanel(SawdClient client, int graph_index)
	{
		this.client = client;
		this.graph_index = graph_index;

		String title = client.get_graph_attribute(graph_index, "name");
		if (title.length() == 0)
			title = "index: " + graph_index;
		setTitle(title);

		model = new DefaultGraphModel();
		graph = new JGraph(model);
		load_graph();
		JScrollPane scroll_pane = new JScrollPane(graph);
		
		JTabbedPane attribute_browser = setupAttributeBrowser();
		load_attributes();

		JSplitPane split_pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll_pane, attribute_browser);
		split_pane.setResizeWeight(1.0);
		getContentPane().add(split_pane);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setPreferredSize(new Dimension(450, 250));
		pack();

		myGraphSelectionListener = new MyGraphSelectionListener();
		graph.addGraphSelectionListener(myGraphSelectionListener);
		myNodesListSelectionListener = new MyNodesListSelectionListener();
		node_table.getSelectionModel().addListSelectionListener(myNodesListSelectionListener);
		myEdgesListSelectionListener = new MyEdgesListSelectionListener();
		edge_table.getSelectionModel().addListSelectionListener(myEdgesListSelectionListener);
	}

	class IndexHolder
	{
		public int index;
		public String name;
		public boolean is_edge;

		public IndexHolder(int index, String name, boolean is_edge)
		{
			this.index = index;
			this.name = name;
			this.is_edge = is_edge;
		}

		public String toString()
		{
			return name;
		}
	}

	double currentX = 0.0;
	double currentY = 0.0;
	final double MAX_WIDTH = 300.0;
	final double INCREMENT_X = 10.0;
	final double INCREMENT_Y = 10.0;

	public void load_graph()
	{
		model.remove(DefaultGraphModel.getAll(model));

		int[] node_indices = client.list_nodes(graph_index);
		DefaultGraphCell[] cells = new DefaultGraphCell[node_indices.length];
		for (int i = 0; i < node_indices.length; i++)
		{
			String name = client.get_node_attribute(graph_index, node_indices[i], "name");
			double x = 0.0, y = 0.0;
			try
			{
				x = Double.parseDouble(client.get_node_attribute(graph_index, node_indices[i], "x"));
				y = Double.parseDouble(client.get_node_attribute(graph_index, node_indices[i], "y"));
			}
			catch (NumberFormatException e)
			{
				x = currentX;
				y = currentY;
				updateCurrentXY();
			}
			cells[i] = createVertex(new IndexHolder(node_indices[i], name, false), x * 7, y * 7, 40, 40, Color.decode("#ff9900"), true);
		}
		graph.getGraphLayoutCache().insert(cells);

		int[] edge_indices = client.list_edges(graph_index);
		DefaultEdge[] edges = new DefaultEdge[edge_indices.length];
		for (int i = 0; i < edge_indices.length; i++)
		{
			edges[i] = new DefaultEdge(new IndexHolder(edge_indices[i], "", true));
			int source = client.get_edge_source(graph_index, edge_indices[i]);
			int target = client.get_edge_target(graph_index, edge_indices[i]);
			edges[i].setSource(cells[source].getChildAt(0));
			edges[i].setTarget(cells[target].getChildAt(0));
		}
		graph.getGraphLayoutCache().insert(edges);

		graph.clearSelection();
	}

	private void updateCurrentXY()
	{
		currentX += INCREMENT_X;
		if (currentX > MAX_WIDTH)
		{
			currentX = 0.0;
			currentY += INCREMENT_Y;
		}
	}

	private static DefaultGraphCell createVertex(Object name, double x,
			double y, double w, double h, Color bg, boolean raised) {

		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(name);

		// Set bounds
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, h));

		// Set fill color
		if (bg != null) {
			GraphConstants.setGradientColor(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		// Set raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory
					.createRaisedBevelBorder());
		else
			// Set black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);

		// Add a Floating Port
		cell.addPort();

		return cell;
	}

	class MyTableModel extends DefaultTableModel
	{
		public MyTableModel()
		{
			super();
		}

		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}

	private JTabbedPane setupAttributeBrowser()
	{
		graph_attributes = new MyTableModel();
		node_attributes = new MyTableModel();
		edge_attributes = new MyTableModel();

		graph_table = new JTable(graph_attributes);
		node_table = new JTable(node_attributes);
		edge_table = new JTable(edge_attributes);

		JTabbedPane tabbed_pane = new JTabbedPane();
		tabbed_pane.add(new JScrollPane(graph_table), "Graph Attributes");
		tabbed_pane.add(new JScrollPane(node_table), "Node Attributes");
		tabbed_pane.add(new JScrollPane(edge_table), "Edge Attributes");

		return tabbed_pane;
	}

	public void load_attributes()
	{
		// load graph attributes

		{
			String[] attribute_names = client.get_graph_attribute_names(graph_index);
			String[] column_names = {"Attribute", "Value"};
			String[][] rows = new String[attribute_names.length + 1][2];

			rows[0][0] = "Index";
			rows[0][1] = Integer.toString(graph_index);
			for (int i = 0; i < attribute_names.length; i++)
			{
				rows[i + 1][0] = attribute_names[i];
				rows[i + 1][1] = client.get_graph_attribute(graph_index, attribute_names[i]);
			}

			graph_attributes.setDataVector(rows, column_names);
		}

		// load node attributes
		
		{
			String[] attribute_names = client.get_node_attribute_names(graph_index);
			int[] node_indices = client.list_nodes(graph_index);

			String[] column_names = new String[attribute_names.length + 1];
			column_names[0] = "Index";
			for (int i = 0; i < attribute_names.length; i++)
				column_names[i + 1] = attribute_names[i];

			String[][] rows = new String[node_indices.length][column_names.length];
			for (int i = 0; i < node_indices.length; i++)
			{
				rows[i][0] = Integer.toString(node_indices[i]);
				for (int j = 0; j < attribute_names.length; j++)
				{
					String value = client.get_node_attribute(graph_index, node_indices[i], attribute_names[j]);
					rows[i][j + 1] = value;
				}
			}

			node_attributes.setDataVector(rows, column_names);
		}
		
		// load edge attributes
		
		{
			String[] attribute_names = client.get_edge_attribute_names(graph_index);
			int[] edge_indices = client.list_edges(graph_index);

			String[] column_names = new String[attribute_names.length + 1];
			column_names[0] = "Index";
			for (int i = 0; i < attribute_names.length; i++)
				column_names[i + 1] = attribute_names[i];

			String[][] rows = new String[edge_indices.length][column_names.length];
			for (int i = 0; i < edge_indices.length; i++)
			{
				rows[i][0] = Integer.toString(edge_indices[i]);
				for (int j = 0; j < attribute_names.length; j++)
				{
					String value = client.get_edge_attribute(graph_index, edge_indices[i], attribute_names[j]);
					rows[i][j + 1] = value;
				}
			}

			edge_attributes.setDataVector(rows, column_names);
		}

	}

	private void freezeGraphSelectionListener()
	{
		myGraphSelectionListener.freeze();
	}

	private void thawGraphSelectionListener()
	{
		myGraphSelectionListener.thaw();
	}

	private void freezeListSelectionListener()
	{
		myNodesListSelectionListener.freeze();
		myEdgesListSelectionListener.freeze();
	}

	private void thawListSelectionListener()
	{
		myNodesListSelectionListener.thaw();
		myEdgesListSelectionListener.thaw();
	}

	public double getScale()
	{
		return graph.getScale();
	}

	public void setScale(double scale)
	{
		graph.setScale(scale);
	}

	class MyGraphSelectionListener implements GraphSelectionListener
	{
		boolean frozen = false;

		public void valueChanged(GraphSelectionEvent e)
		{
			if (frozen)
				return;

			freezeListSelectionListener();

			boolean[] selected_nodes = new boolean[node_attributes.getRowCount()];
			boolean[] selected_edges = new boolean[edge_attributes.getRowCount()];

			Object[] cells = graph.getSelectionCells();
			for (int i = 0; i < cells.length; i++)
			{
				DefaultGraphCell cell = (DefaultGraphCell) cells[i];
				IndexHolder indexHolder = (IndexHolder) cell.getUserObject();
				if (indexHolder.is_edge)
				{
					for (int j = 0; j < edge_attributes.getRowCount(); j++)
					{
						if (edge_attributes.getValueAt(j, 0).equals(Integer.toString(indexHolder.index)))
						{
							selected_edges[j] = true;
						}
					}
				}
				else
				{
					for (int j = 0; j < node_attributes.getRowCount(); j++)
					{
						if (node_attributes.getValueAt(j, 0).equals(Integer.toString(indexHolder.index)))
						{
							selected_nodes[j] = true;
						}
					}
				}
			}

			node_table.clearSelection();
			for (int i = 0; i < selected_nodes.length; i++)
			{
				if (selected_nodes[i])
				{
					node_table.addRowSelectionInterval(i, i);
				}
			}

			edge_table.clearSelection();
			for (int i = 0; i < selected_edges.length; i++)
			{
				if (selected_edges[i])
				{
					edge_table.addRowSelectionInterval(i, i);
				}
			}

			thawListSelectionListener();

		}

		public void freeze()
		{
			frozen = true;
		}

		public void thaw()
		{
			frozen = false;
		}
	}

	class MyNodesListSelectionListener implements ListSelectionListener
	{
		boolean frozen = false;

		public void valueChanged(ListSelectionEvent e)
		{
			if (frozen)
				return;


			freezeGraphSelectionListener();

			Object[] cells = DefaultGraphModel.getAll(model);
			int[] selected_rows = node_table.getSelectedRows();
			Object[] selected_cells = new Object[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++)
			{
				int selected_row = selected_rows[i];
				int node_index = Integer.parseInt((String) node_attributes.getValueAt(selected_row, 0));
				for (int j = 0; j < cells.length; j++)
				{
					DefaultGraphCell cell = (DefaultGraphCell) cells[j];
					IndexHolder indexHolder = (IndexHolder) cell.getUserObject();
					if (indexHolder == null)
						continue;

					if (!indexHolder.is_edge && indexHolder.index == node_index)
						selected_cells[i] = cells[j];
				}
			}

			graph.clearSelection();
			graph.setSelectionCells(selected_cells);

			thawGraphSelectionListener();
		}

		public void freeze()
		{
			frozen = true;
		}

		public void thaw()
		{
			frozen = false;
		}
	}

	class MyEdgesListSelectionListener implements ListSelectionListener
	{
		boolean frozen = false;

		public void valueChanged(ListSelectionEvent e)
		{
			if (frozen)
				return;

			freezeGraphSelectionListener();

			Object[] cells = DefaultGraphModel.getAll(model);
			int[] selected_rows = edge_table.getSelectedRows();
			Object[] selected_cells = new Object[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++)
			{
				int selected_row = selected_rows[i];
				int edge_index = Integer.parseInt((String) edge_attributes.getValueAt(selected_row, 0));
				for (int j = 0; j < cells.length; j++)
				{
					DefaultGraphCell cell = (DefaultGraphCell) cells[j];
					IndexHolder indexHolder = (IndexHolder) cell.getUserObject();
					if (indexHolder == null)
						continue;

					if (indexHolder.is_edge && indexHolder.index == edge_index)
						selected_cells[i] = cells[j];
				}
			}

			graph.clearSelection();
			graph.setSelectionCells(selected_cells);

			thawGraphSelectionListener();
		}

		public void freeze()
		{
			frozen = true;
		}

		public void thaw()
		{
			frozen = false;
		}
	}

}
