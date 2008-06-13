package SawdVisualizer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class GraphsList extends JPanel
{
	private JTable table;
	private DefaultTableModel table_model;
	private SessionWindow parent_session;
	private ActionListener listener = null;

	public GraphsList(SessionWindow parent_session)
	{
		super(new GridLayout(1,1));
		this.parent_session = parent_session;

		table_model = new MyTableModel();
		table = new JTable(table_model);
		table.addMouseListener(new ListMouseListener());
		JScrollPane scroll_pane = new JScrollPane(table);
		add(scroll_pane);

		load();
	}

	public void load()
	{
		SawdClient client = parent_session.getClient();
		String[] header = {"Graphs"};
		if (client == null)
		{
			String[][] items = {{"<html><i>Not connected</i></html>"}};
			table_model.setDataVector(items, header);
			table.setEnabled(false);
			table.setCellSelectionEnabled(false);
		}
		else
		{
			int[] graph_indices = client.list_graphs();
			if (graph_indices.length == 0)
			{
				String[][] items = {{"<html><i>No graphs</i></html>"}};
				table_model.setDataVector(items, header);
				table.setEnabled(false);
				table.setCellSelectionEnabled(false);
			}
			else
			{
				GraphsListItem[][] items = new GraphsListItem[graph_indices.length][1];
				for (int i = 0; i < graph_indices.length; i++)
					items[i][0] = new GraphsListItem(client, graph_indices[i]);
				table_model.setDataVector(items, header);
				table.setEnabled(true);
				table.setCellSelectionEnabled(true);
			}
		}
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

	class GraphsListItem
	{
		public int index;
		public String name;

		public GraphsListItem(SawdClient client, int index)
		{
			this.index = index;
			this.name = client.get_graph_attribute(index, "name");
		}

		public String toString()
		{
			if (name == null || name.length() == 0)
				return "index: " + index;
			else
				return name;
		}
	}

	class ListMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if (e.getClickCount() != 2)
				return;

			if (listener == null)
				return;

			ActionEvent event = new ActionEvent(GraphsList.this, ActionEvent.ACTION_PERFORMED, null);
			listener.actionPerformed(event);
		}
	}

	public void setActionListener(ActionListener l)
	{
		listener = l;
	}

	public int getSelectedGraphIndex()
	{
		int row = table.getSelectedRow();
		if (row < 0)
			return Integer.MIN_VALUE;

		GraphsListItem item = (GraphsListItem) table_model.getValueAt(row, 0);
		return item.index;
	}
}
