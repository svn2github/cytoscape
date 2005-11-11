package browser;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.cytopanels.CytoPanelState;
import exesto.AttributeTags;
import giny.model.GraphObject;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import java.awt.Font;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JCheckBoxMenuItem;

public class AttributePanel2 extends JPanel implements PropertyChangeListener,
		ListSelectionListener, ListDataListener, ActionListener {

	// Global Variables
	CyAttributes data;
	DataTableModel tableModel;

	// create new attribute
	JTextField newAttField;
	JButton newAttButton;
	JComboBox newAttType;

	// attributes
	// JList attributeList;

	// tags
	JList tagList;
	JButton addToTag;
	JButton removeFromTag;
	JTextField newTag;
	JButton newTagButton;

	MultiDataEditAction edit;

	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);
	private JPopupMenu jPopupMenu = null;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JPopupMenu jPopupMenu1 = null;
	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenuItem jMenuItem2 = null;
	private JMenuItem jMenuItem3 = null;
	private JToolBar jToolBar = null;
	private JButton jButton = null;
	private JList attributeList = null;
	private JButton jButton1 = null;
	private JLabel jLabel = null;
	
	private int graphObjectType;
	

	public AttributePanel2() {
		super();
		// TODO Auto-generated constructor stub
		initialize(null);
	}

	public AttributePanel2(CyAttributes data, AttributeModel a_model,
			LabelModel l_model, int got) {
		this.data = data;
		this.graphObjectType = got;
		
		initialize(a_model);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(AttributeModel a_model) {
		this.setLayout(new BorderLayout());
		this.setSize(626, 32);
		this.setPreferredSize(new java.awt.Dimension(210, 35));
		this.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.add(getJToolBar(), java.awt.BorderLayout.NORTH);

		getJPopupMenu(a_model);
		getJPopupMenu1();

	}

	public String getSelectedAttribute() {
		return attributeList.getSelectedValue().toString();
	}

	public void setTableModel(DataTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public void valueChanged(ListSelectionEvent e) {

		try {

			if (e.getSource() == attributeList) {
				Object[] atts = attributeList.getSelectedValues();
				tableModel.setTableDataAttributes(Arrays.asList(atts));
			}

			if (e.getSource() == tagList) {
				String tag = tagList.getSelectedValue().toString();
				Set atts = AttributeTags.getAttributesByTag(data, tag);
				int[] indices = new int[atts.size()];

				int count = 0;
				for (Iterator i = atts.iterator(); i.hasNext();) {
					int ind = attributeList.getNextMatch((String) i.next(), 0,
							javax.swing.text.Position.Bias.Forward);
					indices[count] = ind;
					count++;
				}

				attributeList.setSelectedIndices(indices);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void contentsChanged(ListDataEvent e) {
	}

	public void intervalAdded(ListDataEvent e) {
		// handleEvent(e);
	}

	public void intervalRemoved(ListDataEvent e) {
		// handleEvent(e);
	}

	public void propertyChange(PropertyChangeEvent e) {
		// updateLists();
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method initializes jPopupMenu
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu(AttributeModel a_model) {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.add(getJScrollPane(a_model));
		}
		return jPopupMenu;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane(AttributeModel a_model) {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(220, 200));
			jScrollPane.setViewportView(getJList1(a_model));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPopupMenu1
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu1() {
		if (jPopupMenu1 == null) {
			jPopupMenu1 = new JPopupMenu();
			jPopupMenu1.add(getJMenuItem1());
			jPopupMenu1.add(getJMenuItem());
			jPopupMenu1.add(getJMenuItem2());
			jPopupMenu1.add(getJMenuItem3());
		}
		return jPopupMenu1;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem() {
		if (jMenuItem == null) {
			jMenuItem = new JMenuItem();
			jMenuItem.setText("String Attribute");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed() s"); // TODO
					// Auto-generated
					// Event stub
					// actionPerformed()
					createNewAttribute("String");

				}
			});
		}
		return jMenuItem;
	}

	/**
	 * This method initializes jMenuItem1
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem1() {
		if (jMenuItem1 == null) {
			jMenuItem1 = new JMenuItem();
			jMenuItem1.setText("Integer Attribute");
			jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// TODO Auto-generated Event stub actionPerformed()

					createNewAttribute("Integer");
				}
			});
		}
		return jMenuItem1;
	}

	/**
	 * This method initializes jMenuItem2
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem2() {
		if (jMenuItem2 == null) {
			jMenuItem2 = new JMenuItem();
			jMenuItem2.setText("Floating Point Attribute");
			jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO
					// Auto-generated
					// Event stub
					// actionPerformed()
					createNewAttribute("Floating Point");
				}
			});
		}
		return jMenuItem2;
	}

	/**
	 * This method initializes jMenuItem3
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItem3() {
		if (jMenuItem3 == null) {
			jMenuItem3 = new JMenuItem();
			jMenuItem3.setText("Boolean Attribute");
			jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("actionPerformed()"); // TODO
					// Auto-generated
					// Event stub
					// actionPerformed()
					createNewAttribute("Boolean");
				}
			});
		}
		return jMenuItem3;
	}

	/**
	 * This method initializes jToolBar
	 * 
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (jToolBar == null) {
			jLabel = new JLabel();
			jLabel.setText("  ");
			jLabel.setPreferredSize(new java.awt.Dimension(12, 15));
			jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jToolBar = new JToolBar();
			jToolBar.setPreferredSize(new java.awt.Dimension(200, 28));
			jToolBar.setFloatable(false);
			jToolBar.setOrientation(JToolBar.HORIZONTAL);
			jToolBar.add(getJButton());
			jToolBar.add(jLabel);
			jToolBar.add(getJButton1());
		}
		return jToolBar;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton
					.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
							12));
			jButton
					.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jButton.setText("Select Attributes");
			jButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// TODO Auto-generated Event stub mouseClicked()

					jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jList1
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList1(AttributeModel a_model) {
		if (attributeList == null) {
			attributeList = new JList();

			attributeList = new JList(a_model);
			attributeList.addMouseListener(new java.awt.event.MouseAdapter() {
				
				ArrayList       indices = new ArrayList();
				
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// TODO Auto-generated Event stub mouseClicked()
					if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
						
							jPopupMenu.setVisible(false);
					} else {
						int index = attributeList.locationToIndex(e.getPoint());
						 
                        Integer indexObj = new Integer(index);

                        //is this selected? if so remove it.
                        if (indices.contains(indexObj)) {
                                indices.remove(indexObj);
                        }

                        //otherwise add it to our list
                        else indices.add(indexObj);

                        //copy to an int array
                        int[] arr = new int[indices.size()];
                        for (int i = 0; i < arr.length; i++) {
                                int item = ((Integer) indices.get(i)).intValue();
                                arr[i] = item;
                        }
                        //set selected indices
                        attributeList.setSelectedIndices(arr);
					}

				}
			});
			attributeList.addListSelectionListener(this);
			attributeList
					.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		}
		return attributeList;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Create New Attribute");
			jButton1.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
					12));
			jButton1
					.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			jButton1.setSize(new java.awt.Dimension(143, 27));
			jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					// TODO Auto-generated Event stub mouseClicked()
					jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
				}
			});
		}
		return jButton1;
	}

	// Create a whole new attribute and set a default value.
	//
	private void createNewAttribute(String type) {

		String name = JOptionPane.showInputDialog(this,
				"Please enter new attribute name: ", "Create New " + type
						+ " Attribute", JOptionPane.QUESTION_MESSAGE);
		
		if (name != null) {
			//Object objects = tableModel.getObjects();
			Object objects = null;
			
			if(graphObjectType == DataTable.NODES) {
				objects = Cytoscape.getCyNodesList();
			} else {
				objects = Cytoscape.getCyEdgesList();
			}
			
			for (Iterator i = ((List) objects).iterator(); i.hasNext();) {
				GraphObject go = (GraphObject) i.next();
				if (type.equals("String")) {

					data.setAttribute(go.getIdentifier(), name, new String(""));
				} else if (type.equals("Floating Point")) {
					data.setAttribute(go.getIdentifier(), name, new Double(0));
				} else if (type.equals("Integer")) {
					data.setAttribute(go.getIdentifier(), name, new Integer(0));
				} else if (type.equals("Boolean")) {
					data.setAttribute(go.getIdentifier(), name, new Boolean(
							false));
				} else {
					data.setAttribute(go.getIdentifier(), name, new String(""));
				}

			}

		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
