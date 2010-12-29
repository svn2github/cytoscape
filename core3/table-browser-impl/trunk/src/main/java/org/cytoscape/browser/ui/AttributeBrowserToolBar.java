/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.browser.ui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.util.HashMap;

import org.cytoscape.browser.internal.BrowserTable;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.CheckBoxJList;


public class AttributeBrowserToolBar extends JPanel implements PopupMenuListener {
	private static final long serialVersionUID = -508393701912596399L;

	private CyTable attrs;
	private final JTable table;
	private String attributeType = null;
	private List<String> orderedCol;

	/**
	 *  GUI components
	 */
	private JPopupMenu attributeSelectionPopupMenu = null;
	private JScrollPane jScrollPane = null;
	private JPopupMenu jPopupMenu = null;

	private JMenuItem jMenuItemStringAttribute = null;
	private JMenuItem jMenuItemIntegerAttribute = null;
	private JMenuItem jMenuItemLongIntegerAttribute = null;
	private JMenuItem jMenuItemFloatingPointAttribute = null;
	private JMenuItem jMenuItemBooleanAttribute = null;

	private JMenuItem jMenuItemStringListAttribute = null;
	private JMenuItem jMenuItemIntegerListAttribute = null;
	private JMenuItem jMenuItemLongIntegerListAttribute = null;
	private JMenuItem jMenuItemFloatingPointListAttribute = null;
	private JMenuItem jMenuItemBooleanListAttribute = null;

	private JToolBar browserToolBar = null;
	private JButton selectButton = null;
	private CheckBoxJList attributeList = null;
	private JList attrDeletionList = null;
	private JButton createNewAttributeButton = null;
	private JButton deleteAttributeButton = null;
	private JButton selectAllAttributesButton = null;
	private JButton unselectAllAttributesButton = null;
	private JButton matrixButton = null;
	private JButton importButton = null;

//	private ModDialog modDialog;
//	private FormulaBuilderDialog formulaBuilderDialog;

	public AttributeBrowserToolBar(final BrowserTable table) {
		this.table = table;
		this.orderedCol = orderedCol;

		initializeGUI();
	}

	public void setAttrs(final CyTable attrs) {
		this.attrs = attrs;
		createNewAttributeButton.setEnabled(attrs != null);
		deleteAttributeButton.setEnabled(attrs != null);
	}

	private void initializeGUI() {
		this.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(210, 32));
		this.add(getJToolBar(), java.awt.BorderLayout.CENTER);

//		getAttributeSelectionPopupMenu();
		getJPopupMenu();

//		modDialog = new ModDialog(tableModel, objectType, Cytoscape.getDesktop());
//		attrModButton.setVisible(objectType != NETWORK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getToBeDeletedAttribute() {
		return attrDeletionList.getSelectedValue().toString();
	}

	/**
	 * This method initializes jPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getAttributeSelectionPopupMenu() {
		if (attributeSelectionPopupMenu == null) {
			attributeSelectionPopupMenu = new JPopupMenu();
//			attributeSelectionPopupMenu.add(getJScrollPane());
			attributeSelectionPopupMenu.addPopupMenuListener(this);
		}

		return attributeSelectionPopupMenu;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
/*	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(600, 300));
			jScrollPane.setViewportView(getSelectedAttributeList());
		}

		return jScrollPane;
	}
*/

	/**
	 * This method initializes jPopupMenu
	 *
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getJPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			jPopupMenu.add(getJMenuItemIntegerAttribute());
			jPopupMenu.add(getJMenuItemLongIntegerAttribute());
			jPopupMenu.add(getJMenuItemStringAttribute());
			jPopupMenu.add(getJMenuItemFloatingPointAttribute());
			jPopupMenu.add(getJMenuItemBooleanAttribute());
			jPopupMenu.add(getJMenuItemIntegerListAttribute());
			jPopupMenu.add(getJMenuItemLongIntegerListAttribute());
			jPopupMenu.add(getJMenuItemStringListAttribute());
			jPopupMenu.add(getJMenuItemFloatingPointListAttribute());
			jPopupMenu.add(getJMenuItemBooleanListAttribute());
		}

		return jPopupMenu;
	}

	/**
	 * This method initializes jMenuItemStringAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemStringAttribute() {
		if (jMenuItemStringAttribute == null) {
			jMenuItemStringAttribute = new JMenuItem();
			jMenuItemStringAttribute.setText("String Attribute");
			jMenuItemStringAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("String");
					}
				});
		}

		return jMenuItemStringAttribute;
	}

	/**
	 * This method initializes jMenuItemIntegerAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemIntegerAttribute() {
		if (jMenuItemIntegerAttribute == null) {
			jMenuItemIntegerAttribute = new JMenuItem();
			jMenuItemIntegerAttribute.setText("Integer Attribute");
			jMenuItemIntegerAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Integer");
					}
				});
		}

		return jMenuItemIntegerAttribute;
	}

	/**
	 * This method initializes jMenuItemLongIntegerAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemLongIntegerAttribute() {
		if (jMenuItemLongIntegerAttribute == null) {
			jMenuItemLongIntegerAttribute = new JMenuItem();
			jMenuItemLongIntegerAttribute.setText("Long Integer Attribute");
			jMenuItemLongIntegerAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Long Integer");
					}
				});
		}

		return jMenuItemLongIntegerAttribute;
	}

	/**
	 * This method initializes jMenuItemFloatingPointAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemFloatingPointAttribute() {
		if (jMenuItemFloatingPointAttribute == null) {
			jMenuItemFloatingPointAttribute = new JMenuItem();
			jMenuItemFloatingPointAttribute.setText("Floating Point Attribute");
			jMenuItemFloatingPointAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Floating Point");
					}
				});
		}

		return jMenuItemFloatingPointAttribute;
	}

	/**
	 * This method initializes jMenuItemBooleanAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemBooleanAttribute() {
		if (jMenuItemBooleanAttribute == null) {
			jMenuItemBooleanAttribute = new JMenuItem();
			jMenuItemBooleanAttribute.setText("Boolean Attribute");
			jMenuItemBooleanAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Boolean");
					}
				});
		}

		return jMenuItemBooleanAttribute;
	}

	/**
	 * This method initializes jMenuItemStringListAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemStringListAttribute() {
		if (jMenuItemStringListAttribute == null) {
			jMenuItemStringListAttribute = new JMenuItem();
			jMenuItemStringListAttribute.setText("String List Attribute");
			jMenuItemStringListAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("String List");
					}
				});
		}

		return jMenuItemStringListAttribute;
	}

	/**
	 * This method initializes jMenuItemIntegerListAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemIntegerListAttribute() {
		if (jMenuItemIntegerListAttribute == null) {
			jMenuItemIntegerListAttribute = new JMenuItem();
			jMenuItemIntegerListAttribute.setText("Integer List Attribute");
			jMenuItemIntegerListAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Integer List");
					}
				});
		}

		return jMenuItemIntegerListAttribute;
	}

	/**
	 * This method initializes jMenuItemLongIntegerListAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemLongIntegerListAttribute() {
		if (jMenuItemLongIntegerListAttribute == null) {
			jMenuItemLongIntegerListAttribute = new JMenuItem();
			jMenuItemLongIntegerListAttribute.setText("Long Integer List Attribute");
			jMenuItemLongIntegerListAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Integer List");
					}
				});
		}

		return jMenuItemLongIntegerListAttribute;
	}

	/**
	 * This method initializes jMenuItemFloatingPointListAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemFloatingPointListAttribute() {
		if (jMenuItemFloatingPointListAttribute == null) {
			jMenuItemFloatingPointListAttribute = new JMenuItem();
			jMenuItemFloatingPointListAttribute.setText("Floating Point List Attribute");
			jMenuItemFloatingPointListAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Floating Point List");
					}
				});
		}

		return jMenuItemFloatingPointListAttribute;
	}

	/**
	 * This method initializes jMenuItemBooleanListAttribute
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemBooleanListAttribute() {
		if (jMenuItemBooleanListAttribute == null) {
			jMenuItemBooleanListAttribute = new JMenuItem();
			jMenuItemBooleanListAttribute.setText("Boolean List Attribute");
			jMenuItemBooleanListAttribute.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						createNewAttribute("Boolean List");
					}
				});
		}

		return jMenuItemBooleanListAttribute;
	}

	/**
	 * This method initializes jToolBar
	 *
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJToolBar() {
		if (browserToolBar == null) {
			browserToolBar = new JToolBar();
			browserToolBar.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
//					AttributeBrowser.getPropertyChangeSupport().firePropertyChange(AttributeBrowser.CLEAR_INTERNAL_SELECTION, null, objectType);
				}
			});
			browserToolBar.setMargin(new java.awt.Insets(0, 0, 3, 0));
			browserToolBar.setPreferredSize(new Dimension(200, 30));
			browserToolBar.setFloatable(false);
			browserToolBar.setOrientation(JToolBar.HORIZONTAL);

			final GroupLayout buttonBarLayout = new GroupLayout(browserToolBar);
			browserToolBar.setLayout(buttonBarLayout);

			// Layout information.
			buttonBarLayout.setHorizontalGroup(buttonBarLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							   .addGroup(buttonBarLayout.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)

/*								.addComponent(getSelectButton())
								.addPreferredGap(ComponentPlacement.RELATED)
*/
								.addComponent(getNewButton())
								.addPreferredGap(ComponentPlacement.RELATED)
/*								.add(getSelectAllButton())
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(getUnselectAllButton())
*/
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(getDeleteButton())
/*
								.addPreferredGap(ComponentPlacement.RELATED,
										 28,
										 Short.MAX_VALUE)
								.addComponent(getAttrModButton(),
								     GroupLayout.PREFERRED_SIZE,
								     28,
								     GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(getFunctionBuilderButton(),
								     GroupLayout.PREFERRED_SIZE,
								     28,
								     GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)

								.addComponent(getImportButton(),
								     GroupLayout.PREFERRED_SIZE,
								     28,
								     GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(getMatrixButton())
								.addPreferredGap(ComponentPlacement.RELATED)*/));
			buttonBarLayout.setVerticalGroup(buttonBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
/*							 .addComponent(selectButton,
								       javax.swing.GroupLayout.Alignment.CENTER,
								       javax.swing.GroupLayout.PREFERRED_SIZE,
								       27,
								       javax.swing.GroupLayout.PREFERRED_SIZE)
*/
							 .addComponent(createNewAttributeButton,
								       javax.swing.GroupLayout.Alignment.CENTER,
								       javax.swing.GroupLayout.DEFAULT_SIZE,
								       27, Short.MAX_VALUE)
/*							 .addComponent(selectAllAttributesButton,
								       javax.swing.GroupLayout.Alignment.CENTER,
								       javax.swing.GroupLayout.DEFAULT_SIZE,
								       27, Short.MAX_VALUE)
							 .addComponent(unselectAllAttributesButton,
								       javax.swing.GroupLayout.Alignment.CENTER,
								       javax.swing.GroupLayout.DEFAULT_SIZE,
								       27, Short.MAX_VALUE)
*/
							 .addComponent(deleteAttributeButton,
								       javax.swing.GroupLayout.Alignment.CENTER,
								       javax.swing.GroupLayout.DEFAULT_SIZE,
								       27, Short.MAX_VALUE)
/*
							 .addGroup(buttonBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
							      .addComponent(matrixButton,
								   javax.swing.GroupLayout.PREFERRED_SIZE,
								   27,
								   javax.swing.GroupLayout.PREFERRED_SIZE)
							      .addComponent(importButton,
								   javax.swing.GroupLayout.PREFERRED_SIZE,
								   27,
								   javax.swing.GroupLayout.PREFERRED_SIZE)
							      .addComponent(attrModButton,
								   javax.swing.GroupLayout.PREFERRED_SIZE,
								   27,
								   javax.swing.GroupLayout.PREFERRED_SIZE)
							      .addComponent(formulaBuilderButton,
								   javax.swing.GroupLayout.PREFERRED_SIZE,
								   27,
								   javax.swing.GroupLayout.PREFERRED_SIZE))*/);
		}

		return browserToolBar;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSelectButton() {
		if (selectButton == null) {
			selectButton = new JButton();
			selectButton.setBorder(null);
			selectButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			selectButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/stock_select-row.png")));
			selectButton.setToolTipText("Select Attributes");

			selectButton.addMouseListener(new MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						attributeList.setSelectedItems(orderedCol);
						attributeSelectionPopupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				});
		}

		return selectButton;
	}

	private JButton getImportButton() {
		if (importButton == null) {
			importButton = new JButton();
			importButton.setBorder(null);
			importButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/stock_open.png")));
			importButton.setToolTipText("Import attributes from file...");
			importButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			importButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
//						importAttributes();
					}
				});
		}

		return importButton;
	}

	private JButton getMatrixButton() {
		if (matrixButton == null) {
			matrixButton = new JButton();
			matrixButton.setBorder(null);
			matrixButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			matrixButton.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/microarray_24.png")));
			matrixButton.setToolTipText("Import Expression Matrix Data...");

			matrixButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
//						importMatrix();
					}
				});
		}

		return matrixButton;
	}

	private JButton attrModButton = null;

	private JButton getAttrModButton() {
		if (attrModButton == null) {
			attrModButton = new JButton();
			attrModButton.setBorder(null);
			attrModButton.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/stock_insert-columns.png")));
			attrModButton.setToolTipText("Attribute Batch Editor");
			attrModButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

			attrModButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
//						modDialog.setLocationRelativeTo(Cytoscape.getDesktop());
//						modDialog.setVisible(true);
					}
				});
		}

		return attrModButton;
	}

/*
	private JButton formulaBuilderButton = null;

	private JButton getFunctionBuilderButton() {
		if (formulaBuilderButton == null) {
			formulaBuilderButton = new JButton();
			formulaBuilderButton.setBorder(null);
			formulaBuilderButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/fx.png")));
			formulaBuilderButton.setToolTipText("Function Builder");
			formulaBuilderButton.setMargin(new java.awt.Insets(1, 1, 1, 1));

			formulaBuilderButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						// Do not allow opening of the formula builder dialog while a cell is being edited!
						if (table.getCellEditor() != null)
							return;

						final int cellRow = table.getSelectedRow();
						final int cellColumn = table.getSelectedColumn();
						if (cellRow == -1 || cellColumn == -1 || !tableModel.isCellEditable(cellRow, cellColumn))
							JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
							                              "Can't enter a formula w/o a selected cell!",
							                              "Information", JOptionPane.INFORMATION_MESSAGE);
						else {
							final String attrName = getAttribName(cellRow, cellColumn);
							final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
							Util.initAttribNameToTypeMap(objectType, attrName, attribNameToTypeMap);
							formulaBuilderDialog =
								new FormulaBuilderDialog(tableModel, table, objectType, Cytoscape.getDesktop(),
								                         attribNameToTypeMap, attrName);
							formulaBuilderDialog.setLocationRelativeTo(Cytoscape.getDesktop());
							formulaBuilderDialog.setVisible(true);
						}
					}
				});
		}

		return formulaBuilderButton;
	}

	private String getAttribName(final int cellRow, final int cellColumn) {
		if (objectType == NETWORK)
			return ((ValidatedObjectAndEditString)(tableModel.getValueAt(cellRow, 0))).getValidatedObject().toString();
		else
			return tableModel.getColumnName(cellColumn);
	}
*/

/*
	protected void editMetadata() {
		NetworkMetaDataDialog mdd = new NetworkMetaDataDialog(Cytoscape.getDesktop(), false,
		                                                      Cytoscape.getCurrentNetwork());
		mdd.setVisible(true);
	}

	protected void importAttributes() {
		if (objectType == NODES) {
			ImportNodeAttributesAction nodeAction = new ImportNodeAttributesAction();
			nodeAction.actionPerformed(null);
		} else if (objectType == EDGES) {
			ImportEdgeAttributesAction edgeAction = new ImportEdgeAttributesAction();
			edgeAction.actionPerformed(null);
		} else { // case for Network
		}
	}

	protected void importMatrix() {
		ImportExpressionMatrixAction matrixAction = new ImportExpressionMatrixAction();
		matrixAction.actionPerformed(null);
	}
*/

	private JButton getDeleteButton() {
		if (deleteAttributeButton == null) {
			deleteAttributeButton = new JButton();
			deleteAttributeButton.setBorder(null);
			deleteAttributeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			deleteAttributeButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("images/stock_delete.png")));
			deleteAttributeButton.setToolTipText("Delete Attributes...");

			// Create pop-up window for deletion
			deleteAttributeButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						removeAttribute(e);
					}
				});
			deleteAttributeButton.setEnabled(false);
		}

		return deleteAttributeButton;
	}

/*
	private JButton getSelectAllButton() {
		if (selectAllAttributesButton == null) {
			selectAllAttributesButton = new JButton();
			selectAllAttributesButton.setBorder(null);
			selectAllAttributesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			selectAllAttributesButton.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/select_all.png")));
			selectAllAttributesButton.setToolTipText("Select All Attributes");

			selectAllAttributesButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						List<String> existingAttrs = CyAttributesUtils.getVisibleAttributeNames(attrs);
						updateList(existingAttrs);
						try {
							getUpdatedSelectedList();
							tableModel.setTableData(null, orderedCol);
						} catch (Exception ex) {
							attributeList.clearSelection();
						}
					}
				});
		}

		return selectAllAttributesButton;
	}
*/

	private JButton getUnselectAllButton() {
		if (unselectAllAttributesButton == null) {
			unselectAllAttributesButton = new JButton();
			unselectAllAttributesButton.setBorder(null);
			unselectAllAttributesButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			unselectAllAttributesButton.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/unselect_all.png")));
			unselectAllAttributesButton.setToolTipText("Unselect All Attributes");

			unselectAllAttributesButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						final List<String> emptyList = new ArrayList<String>();
						updateList(emptyList);
						try {
							getUpdatedSelectedList();
//							tableModel.setTableData(null, orderedCol);
						} catch (Exception ex) {
							attributeList.clearSelection();
						}
					}
				});
		}

		return unselectAllAttributesButton;
	}

	private void removeAttribute(final MouseEvent e) {
		final String[] attrArray = getAttributeArray();

		final JFrame frame = (JFrame)SwingUtilities.getRoot(this);
		final DeletionDialog dDialog = new DeletionDialog(frame, attrs);

		dDialog.pack();
		dDialog.setLocationRelativeTo(browserToolBar);
		dDialog.setVisible(true);
	}

/*
	private JList getSelectedAttributeList() {
		if (attributeList == null) {
			attributeList = new CheckBoxJList();
			attributeList.setModel(attrModel);
			attributeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			attributeList.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						if (SwingUtilities.isRightMouseButton(e)) {
							attributeSelectionPopupMenu.setVisible(false);
						}
					}
				});
		}

		return attributeList;
	}
*/

	private String[] getAttributeArray() {
		final String primaryKey = attrs.getPrimaryKey();
		final Map<String, Class<?>> nameToTypeMap = attrs.getColumnTypeMap();
		final String[] attributeArray = new String[nameToTypeMap.size() - 1];
		int index = 0;
		for (final String attrName : nameToTypeMap.keySet()) {
			if (!attrName.equals(primaryKey))
				attributeArray[index++] = attrName;
		}
		Arrays.sort(attributeArray);

		return attributeArray;
	}

	/**
	 * This method initializes createNewAttributeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getNewButton() {
		if (createNewAttributeButton == null) {
			createNewAttributeButton = new JButton();
			createNewAttributeButton.setBorder(null);

			createNewAttributeButton.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			createNewAttributeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			createNewAttributeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
			createNewAttributeButton.setToolTipText("Create New Attribute");
			createNewAttributeButton.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/stock_new.png")));
			createNewAttributeButton.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseClicked(java.awt.event.MouseEvent e) {
						if (attrs != null)
							jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				});
			createNewAttributeButton.setEnabled(false);
		}

		return createNewAttributeButton;
	}

	// Create a whole new attribute and set a default value.
	//
	private void createNewAttribute(final String type) {
		final String[] existingAttrs = getAttributeArray();
		String newAttribName = null;
		do {
			newAttribName = JOptionPane.showInputDialog(this, "Please enter new attribute name: ",
								    "Create New " + type + " Attribute",
								    JOptionPane.QUESTION_MESSAGE);
			if (newAttribName == null)
				return;

			if (Arrays.binarySearch(existingAttrs, newAttribName) >= 0) {
				newAttribName = null;
				JOptionPane.showMessageDialog(null,
							      "Attribute " + newAttribName + " already exists.",
							      "Error!", JOptionPane.ERROR_MESSAGE);
			}
		} while (newAttribName == null);

		if (type.equals("String"))
			attrs.createColumn(newAttribName, String.class);
		else if (type.equals("Floating Point"))
			attrs.createColumn(newAttribName, Double.class);
		else if (type.equals("Integer"))
			attrs.createColumn(newAttribName, Integer.class);
		else if (type.equals("Long Integer"))
			attrs.createColumn(newAttribName, Long.class);
		else if (type.equals("Boolean"))
			attrs.createColumn(newAttribName, Boolean.class);
		else if (type.equals("String List")) {
			attrs.createListColumn(newAttribName, String.class);
		} else if (type.equals("Floating Point List")) {
			attrs.createListColumn(newAttribName, Double.class);
		} else if (type.equals("Integer List")) {
			attrs.createListColumn(newAttribName, Integer.class);
		} else if (type.equals("Long Integer List")) {
			attrs.createListColumn(newAttribName, Long.class);
		} else if (type.equals("Boolean List")) {
			attrs.createListColumn(newAttribName, Boolean.class);
		} else
			throw new IllegalArgumentException("unknown attribute type \"" + type + "\"!");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// Update actual table
		try {
			getUpdatedSelectedList();

//			tableModel.setTableData(null, orderedCol);
		} catch (Exception ex) {
			attributeList.clearSelection();
		}
	}

	public List<String> getUpdatedSelectedList() {
/*
		final Object[] selected = attributeList.getSelectedValues();

		orderedCol.remove("ID");

		// determine if orderedCol is ordered (drag and drop column may change the order)
		boolean isColOrdered = true;
		for (int i=0; i< orderedCol.size()-1; i++){
			if (orderedCol.get(i).compareToIgnoreCase(orderedCol.get(i+1)) > 0){
				isColOrdered = false;
			}
		}

		if (isColOrdered){
			// The original columns are in order, leave as is
			orderedCol.clear();
			for (Object colName : selected)
				orderedCol.add(colName.toString());
			return orderedCol;
		}

		// The original columns are out of order

		// Determine the cols to be added
		ArrayList<String> colsToBeAdded = new ArrayList<String>();
		HashMap<String, String> hashMap_orig = new HashMap<String, String>();

		for (int i=0; i< orderedCol.size(); i++){
			hashMap_orig.put(orderedCol.get(i), null);
		}

		for (Object colName : selected) {
			if (!hashMap_orig.containsKey(colName.toString())){
				colsToBeAdded.add(colName.toString());
			}
		}

		// Determine the cols to be deleted
		HashMap<String, String> hashMap_new = new HashMap<String, String>();
		ArrayList<String> colsToBeDeleted = new ArrayList<String>();

		for (Object colName : selected) {
			hashMap_new.put(colName.toString(), null);
		}

		for (int i=0; i< orderedCol.size(); i++){
			if (!hashMap_new.containsKey(orderedCol.get(i))){
				colsToBeDeleted.add(orderedCol.get(i));
			}
		}

		// delete the cols to be deleted from orderedCol
		for (int i=0; i< colsToBeDeleted.size(); i++){
			orderedCol.remove(colsToBeDeleted.get(i));
		}

		// Append the new columns to the end
		for (int i=0; i< colsToBeAdded.size(); i++){
			orderedCol.add(colsToBeAdded.get(i));
		}

		return orderedCol;
*/
		return null;
	}


	public void updateList(List<String> newSelection) {
		orderedCol = newSelection;
		attributeList.setSelectedItems(orderedCol);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// Do nothing
	}
}
