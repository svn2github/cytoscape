/* vim: set ts=2:

  File: LayoutSettingsDialog.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  Dout of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.layout.ui;

import cytoscape.CyNetworkManager;
import cytoscape.view.CySwingApplication;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;

import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.TaskManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;


/**
 *
 * The LayoutSettingsDialog is a dialog that provides an interface into all of the
 * various settings for layout algorithms.  Each CyLayoutAlgorithm must return a single
 * JPanel that provides all of its settings.
 */
public class LayoutSettingsDialog extends JDialog implements ActionListener {
	private final static long serialVersionUID = 1202339874277105L;
	private CyLayoutAlgorithm currentLayout = null;

	// Dialog components
	private JLabel titleLabel; // Our title
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses

	private CyLayouts cyLayouts;
	private CySwingApplication desktop;
	private LayoutMenuManager menuMgr;
	private CyNetworkManager netmgr;
	private TunableInterceptor ti;
	private TaskManager tm;

	/**
	 * Creates a new LayoutSettingsDialog object.
	 */
	public LayoutSettingsDialog(CyLayouts cyLayouts, CySwingApplication desktop, LayoutMenuManager menuMgr, CyNetworkManager netmgr, TunableInterceptor ti,TaskManager tm) {
		super(desktop.getJFrame(), "Layout Settings", false);
		initializeOnce(); // Initialize the components we only do once
		this.cyLayouts = cyLayouts;
		this.desktop = desktop;
		this.menuMgr = menuMgr;
		this.netmgr = netmgr;
		this.ti = ti;
		this.tm = tm;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		String command = e.getActionCommand();

		if (command.equals("done")) {
			setVisible(false);
		} else if (command.equals("execute")) {
			ti.Handle();
			tm.execute( new LayoutTask(currentLayout,netmgr.getCurrentNetworkView()) );
		} else {
			// OK, initialize and display
			initialize();
			pack();
			setLocationRelativeTo(desktop.getJFrame());
			setVisible(true);
		}
	}

	private void initializeOnce() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for the list of algorithms
		JPanel algorithmSelectorPanel = new JPanel();
		algorithmSelector = new JComboBox();
		algorithmSelector.addActionListener(new AlgorithmActionListener());
		algorithmSelectorPanel.add(algorithmSelector);

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder, "Layout Algorithm");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmSelectorPanel.setBorder(titleBorder);
		mainPanel.add(algorithmSelectorPanel);

		// Create a panel for algorithm's content
		this.algorithmPanel = new JPanel();
		mainPanel.add(algorithmPanel);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		JButton doneButton = new JButton("Done");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);

		JButton executeButton = new JButton("Execute Layout");
		executeButton.setActionCommand("execute");
		executeButton.addActionListener(this);

		buttonBox.add(executeButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}

	private void initialize() {
		// Populate the algorithm selector
		algorithmSelector.removeAllItems();

		// Add the "instructions"
		algorithmSelector.setRenderer(new MyItemRenderer());
		algorithmSelector.addItem("Select algorithm to view settings");

		// Get the list of known layout menus
		Set<String> menus = menuMgr.getLayoutMenuNames();
	
		for (String menu : menus) {

			if (menus.size() > 1) {
				algorithmSelector.addItem(menu);
			}

			for (CyLayoutAlgorithm algo : menuMgr.getLayoutsInMenu(menu)) {
				// TODO might want a check here to see if algorithm has any tunables
				algorithmSelector.addItem(algo);
			}
		}
	}


	private class AlgorithmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object o = algorithmSelector.getSelectedItem();
			// if it's a string, that means it's the instructions
			if (!(o instanceof String)) { 
				CyLayoutAlgorithm newLayout = (CyLayoutAlgorithm)o;
				ti.loadTunables(newLayout);
				ti.setParent(mainPanel);
				ti.createUI(newLayout);
				pack();
				currentLayout = newLayout; 
			}
		}
	}

	private class MyItemRenderer extends JLabel implements ListCellRenderer {
		private final static long serialVersionUID = 1202339874266209L;
		public MyItemRenderer() {
		}

		public Component getListCellRendererComponent(JList list, Object value, int index,
		                                              boolean isSelected, boolean cellHasFocus) {
			// If this is a String, we don't want to allow selection.  If this is
			// index 0, we want to set the font 
			Font f = getFont();

			if (value.getClass() == String.class) {
				setFont(f.deriveFont(Font.PLAIN));
				setText((String) value);
				setHorizontalAlignment(CENTER);
				setForeground(Color.GRAY);
				setEnabled(false);
			} else {
				setForeground(list.getForeground());
				setHorizontalAlignment(LEFT);
				setEnabled(true);

				if (isSelected) {
					setFont(f.deriveFont(Font.BOLD));
				} else {
					setFont(f.deriveFont(Font.PLAIN));
				}

				setText(value.toString());
			}

			return this;
		}
	}
}
