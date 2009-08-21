package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeEvent;
import com.vlsolutions.swing.docking.event.DockableStateWillChangeListener;

public class VLDockTest extends JFrame implements ComponentListener {
	// our 4 dockable components
	private MyTextEditor editorPanel;
	private MyTree treePanel;
	private MyGridOfButtons buttonGrid;
	private MyJTable tablePanel;

	// the desktop (which will contain dockables)
	private DockingDesktop desk;

	// byte array used to save a workspace (custom layout of dockables)
	private byte[] savedWorkpace;

	// action used to save the current workspace
	Action saveWorkspaceAction = new AbstractAction("Save Workspace") {
		public void actionPerformed(ActionEvent e) {
			saveWorkspace();
		}
	};

	// action used to reload a workspace
	Action loadWorkspaceAction = new AbstractAction("Reload Workspace") {
		public void actionPerformed(ActionEvent e) {
			loadWorkspace();
		}
	};

	/** Default and only frame constructor */
	public VLDockTest() {

		System.out.println("--------Invoking Docking Frame");
		
		
		try {
			editorPanel = new MyTextEditor();
			System.out.println("--------Invoking Docking Frame 1");
			treePanel = new MyTree();
			System.out.println("--------Invoking Docking Frame 2");
			buttonGrid = new MyGridOfButtons();
			System.out.println("--------Invoking Docking Frame 3");
			tablePanel = new MyJTable();
			System.out.println("--------Invoking Docking Frame 4");

			desk = new DockingDesktop();
			
			System.out.println("--------Invoking Docking Frame 5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("-------- Component Construction done");

		// insert our desktop as the only one component of the frame
		add(desk, BorderLayout.CENTER);

		// set the initial dockable
		desk.addDockable(editorPanel);
		// and layout the others
		desk.split(editorPanel, tablePanel, DockingConstants.SPLIT_LEFT);
		desk.split(editorPanel, buttonGrid, DockingConstants.SPLIT_RIGHT);
		// desk.createTab(treePanel, tablePanel, 1);
		desk.split(tablePanel, treePanel, DockingConstants.SPLIT_BOTTOM);

		// cannot reload before a workspace is saved
		loadWorkspaceAction.setEnabled(false);

		// add sale/reload menus
		JMenuBar menubar = new JMenuBar();
		JMenu actions = new JMenu("Actions");
		menubar.add(actions);
		actions.add(saveWorkspaceAction);
		actions.add(loadWorkspaceAction);

		// listen to dockable state changes before they are commited
		desk
				.addDockableStateWillChangeListener(new DockableStateWillChangeListener() {
					public void dockableStateWillChange(
							DockableStateWillChangeEvent event) {
						DockableState current = event.getCurrentState();
						if (current.getDockable() == editorPanel) {
							if (event.getFutureState().isClosed()) {
								// we are facing a closing of the editorPanel
								event.cancel(); // refuse it
							}
						}
					}
				});
	}
	

	/** Save the current workspace into an instance byte array */
	private void saveWorkspace() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			desk.writeXML(out);
			out.close();
			savedWorkpace = out.toByteArray();
			loadWorkspaceAction.setEnabled(true);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/** Reloads a saved workspace */
	private void loadWorkspace() {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(savedWorkpace);
			desk.readXML(in);
			in.close();
		} catch (Exception ex) {
			// catch all exceptions, including those of the SAXParser
			ex.printStackTrace();
		}
	}

	/**
	 * Inner class describing a dockable text editor.
	 * */
	class MyTextEditor extends JPanel implements Dockable {
		JTextArea textArea = new JTextArea("A Text Area");
		DockKey key = new DockKey("textEditor");

		public MyTextEditor() {
			setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane(textArea);
			jsp.setPreferredSize(new Dimension(300, 400));
			add(jsp, BorderLayout.CENTER);
			// customized display
			key.setName("The Text Area");
			key.setTooltip("This is the text area tooltip");
			
			// customized behaviour
			key.setCloseEnabled(false);
			key.setAutoHideEnabled(false);
			//
			key.setResizeWeight(1.0f); // takes all resizing
		}

		/** implement Dockable */
		public DockKey getDockKey() {
			return key;
		}

		/** implement Dockable */
		public Component getComponent() {
			return this;
		}
	}

	class MyTree extends JPanel implements Dockable {
		JTree tree = new JTree();
		DockKey key = new DockKey("tree");

		public MyTree() {
			setLayout(new BorderLayout());
			JScrollPane jsp = new JScrollPane(tree);
			jsp.setPreferredSize(new Dimension(200, 200));
			add(jsp, BorderLayout.CENTER);
		}

		public DockKey getDockKey() {
			return key;
		}

		public Component getComponent() {
			return this;
		}
	}

	class MyGridOfButtons extends JPanel implements Dockable {
		DockKey key = new DockKey("grid of buttons");

		public MyGridOfButtons() {
			setLayout(new FlowLayout(FlowLayout.LEADING, 3, 3));
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					add(new JButton("btn " + (i * 3 + j)));
				}
			}
			setPreferredSize(new Dimension(200, 300));
		}

		public DockKey getDockKey() {
			return key;
		}

		public Component getComponent() {
			return this;
		}
	}

	class MyJTable extends JPanel implements Dockable {
		JTable table = new JTable();
		DockKey key = new DockKey("table");

		public MyJTable() {
			setLayout(new BorderLayout());
			table.setModel(new DefaultTableModel(5, 5));
			JScrollPane jsp = new JScrollPane(table);
			jsp.setPreferredSize(new Dimension(200, 200));
			add(jsp, BorderLayout.CENTER);
		}

		public DockKey getDockKey() {
			return key;
		}

		public Component getComponent() {
			return this;
		}
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	public void componentResized(ComponentEvent e) {
		System.out.println("############# Resize");
		
	}


	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}
