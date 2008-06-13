package SawdVisualizer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SessionWindow extends JFrame
{
	SawdClient client = null;
	Map<Integer, GraphPanel> graph_panels = new TreeMap<Integer, GraphPanel>();
	GraphsList graphs_list;
	JDesktopPane graphs_pane;

	public SessionWindow()
	{
		JMenuBar menubar = newMenuBar();
		getContentPane().add(menubar, BorderLayout.PAGE_START);

		graphs_list = new GraphsList(this);
		graphs_list.setActionListener(new OpenGraphAction());
		graphs_pane = new JDesktopPane();
		graphs_pane.setBackground(Color.decode("#B0C4DE"));
		graphs_pane.setPreferredSize(new Dimension(500, 500));
		JScrollPane desktop_scroll_pane = new JScrollPane(graphs_pane);
		desktop_scroll_pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		desktop_scroll_pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphs_list, desktop_scroll_pane);
		getContentPane().add(split_pane, BorderLayout.CENTER);

		addWindowListener(new CloseSessionAction());
		endConnection();
		pack();
	}

	private JMenuBar newMenuBar()
	{
		JMenuBar menubar = new JMenuBar();

		// Main menu
		JMenu main_menu = new JMenu("SawdVisualizer");
		menubar.add(main_menu);

		JMenuItem about_item = new JMenuItem("About SawdVisualizer...");
		about_item.addActionListener(new AboutAction());
		main_menu.add(about_item);

		main_menu.addSeparator();

		JMenuItem quit_item = new JMenuItem("Quit SawdVisualizer");
		quit_item.addActionListener(new QuitAction());
		main_menu.add(quit_item);

		// Session menu
		JMenu session_menu = new JMenu("Session");
		menubar.add(session_menu);

		JMenuItem new_session_item = new JMenuItem("New Session...", new ImageIcon(getClass().getResource("/new.png")));
		new_session_item.addActionListener(new NewSessionAction());
		session_menu.add(new_session_item);

		JMenuItem close_session_item = new JMenuItem("Close Session", new ImageIcon(getClass().getResource("/close.png")));
		close_session_item.addActionListener(new CloseSessionAction());
		session_menu.add(close_session_item);

		session_menu.addSeparator();

		JMenuItem connect_item = new JMenuItem("Connect...", new ImageIcon(getClass().getResource("/connect.png")));
		connect_item.addActionListener(new ConnectAction());
		session_menu.add(connect_item);

		session_menu.addSeparator();

		JMenuItem refresh_item = new JMenuItem("Refresh List of Graphs", new ImageIcon(getClass().getResource("/refresh.png")));
		refresh_item.addActionListener(new RefreshAction());
		session_menu.add(refresh_item);

		// Graph menu
		JMenu graph_menu = new JMenu("Graph");
		menubar.add(graph_menu);

		JMenuItem refresh_graph_item = new JMenuItem("Refresh", new ImageIcon(getClass().getResource("/refresh.png")));
		refresh_graph_item.addActionListener(new RefreshGraphAction());
		graph_menu.add(refresh_graph_item);

		graph_menu.addSeparator();

		JMenuItem zoom_in_item = new JMenuItem("Zoom In", new ImageIcon(getClass().getResource("/zoom_in.png")));
		zoom_in_item.addActionListener(new ZoomInAction()); 
		graph_menu.add(zoom_in_item);

		JMenuItem zoom_scale_item = new JMenuItem("Zoom to Scale", new ImageIcon(getClass().getResource("/zoom_scale.png")));
		zoom_scale_item.addActionListener(new ZoomScaleAction()); 
		graph_menu.add(zoom_scale_item);

		JMenuItem zoom_out_item = new JMenuItem("Zoom Out", new ImageIcon(getClass().getResource("/zoom_out.png")));
		zoom_out_item.addActionListener(new ZoomOutAction()); 
		graph_menu.add(zoom_out_item);

		return menubar;
	}


	public SawdClient getClient()
	{
		return client;
	}

	public void closeSession()
	{
		endConnection();
		this.dispose();
	}

	private void showConnectDialog()
	{
		final ConnectDialog connect_dialog = new ConnectDialog(this);
		connect_dialog.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String server = connect_dialog.getServer();
				int port = connect_dialog.getPort();
				connect_dialog.dispose();
				startConnection(server, port);
			}
		});
		connect_dialog.setVisible(true);
	}

	private void startConnection(String server, int port)
	{
		endConnection();

		client = new SawdClient(server, port);
		if (!client.isConnected())
		{
			client = null;
			return;
		}

		setTitle("SawdVisualizer: Connected to \'" + server + ':' + port + '\'');
		graphs_list.load();
	}

	private void endConnection()
	{
		setTitle("SawdVisualizer: Not connected");

		if (client != null)
		{
			for (Integer index : graph_panels.keySet())
			{
				GraphPanel panel = graph_panels.get(index);
				if (panel != null) panel.dispose();
			}
			graph_panels = new TreeMap<Integer, GraphPanel>();
			client.close();
			client = null;
		}

		graphs_list.load();
	}
	
	private class AboutAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			TitleWindow title_window = new TitleWindow(null);
			title_window.setVisible(true);
		}
	}

	private class QuitAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Main.endAllSessions();
		}
	}

	private class NewSessionAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Main.newSession();
		}
	}

	private class ConnectAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			showConnectDialog();
		}
	}

	private class CloseSessionAction extends WindowAdapter implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			closeSession();
		}

		public void windowClosing(WindowEvent e)
		{
			closeSession();
		}
	}

	private class OpenGraphAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Integer graph_index = new Integer(graphs_list.getSelectedGraphIndex());
			if (graph_index.intValue() == Integer.MIN_VALUE)
				return;

			GraphPanel panel;
			if (graph_panels.containsKey(graph_index))
			{
				panel = graph_panels.get(graph_index);
			}
			else
			{
				panel = new GraphPanel(client, graph_index.intValue());
				graph_panels.put(graph_index, panel);
				graphs_pane.add(panel);
			}

			try
			{
				panel.setVisible(true);
				panel.setSelected(true);
			}
			catch (java.beans.PropertyVetoException exception) {}
		}
	}

	private class RefreshAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for (GraphPanel panel : graph_panels.values())
				panel.dispose();
			graph_panels = new TreeMap<Integer, GraphPanel>();
			graphs_list.load();
		}
	}

	private class RefreshGraphAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GraphPanel panel = (GraphPanel) graphs_pane.getSelectedFrame();
			if (panel == null)
				return;
			panel.load_graph();
		}
	}

	private class ZoomInAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GraphPanel panel = (GraphPanel) graphs_pane.getSelectedFrame();
			if (panel == null)
				return;
			panel.setScale(panel.getScale() * 4.0 / 3.0);
		}
	}

	private class ZoomScaleAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GraphPanel panel = (GraphPanel) graphs_pane.getSelectedFrame();
			if (panel == null)
				return;
			panel.setScale(1.0);
		}
	}

	private class ZoomOutAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			GraphPanel panel = (GraphPanel) graphs_pane.getSelectedFrame();
			if (panel == null)
				return;
			panel.setScale(panel.getScale() * 3.0 / 4.0);
		}
	}
}
