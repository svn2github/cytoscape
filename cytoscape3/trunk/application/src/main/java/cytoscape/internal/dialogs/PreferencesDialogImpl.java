/*
  File: PreferencesDialogImpl.java

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
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.internal.dialogs;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.view.vizmap.events.SaveVizmapPropsEvent;
import org.cytoscape.view.vizmap.events.SaveVizmapPropsListener;

import cytoscape.events.PreferencesUpdatedEvent;
import cytoscape.events.PreferencesUpdatedListener;

import cytoscape.Cytoscape;
import cytoscape.CyOperatingContext;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;


/**
 *
 */
public class PreferencesDialogImpl extends JDialog {
	private final static long serialVersionUID = 1202339873396288L;

	int[] selection = null;
	private Properties props;
	private CyOperatingContext context;
	private CyEventHelper eh;

	JScrollPane propsTablePane = new JScrollPane();
	JTable prefsTable = new JTable();
	JPanel propBtnPane = new JPanel(new FlowLayout());
	JPanel okButtonPane = new JPanel(new FlowLayout());
	JPanel vizmapPane = new JPanel(new FlowLayout());
	JPanel cyPropsPane = new JPanel(new FlowLayout());
	JCheckBox saveVizmapBtn = new JCheckBox("Make Current Visual Styles Default", false);
	JCheckBox saveCyPropsBtn = new JCheckBox("Make Current Cytoscape Properties Default", false);
	JTextArea vizmapText = new JTextArea("Only check this option if you want the current visual styles to be defaults in ALL future cytoscape sessions.  Your current visual styles are automatically saved in your Cytoscape session file and won't be lost.");
	JTextArea cyPropsText = new JTextArea("Only check this option if you want the current Cytoscape properties to be defaults in ALL future cytoscape sessions.  Your current Cytoscape properties are automatically saved in your Cytoscape session file and won't be lost.");
	JButton addPropBtn = new JButton("Add");
	JButton deletePropBtn = new JButton("Delete");
	JButton modifyPropBtn = new JButton("Modify");
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");

	/**
	 *
	 */
	public PreferenceTableModel prefsTM = null;
	private ListSelectionModel lsm = null;
	private ListSelectionModel lsmA = null;
	private boolean saveCyPropsAsDefault = false;
	private boolean saveVizmapAsDefault = false;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param tm DOCUMENT ME!
	 * @param preferenceName DOCUMENT ME!
	 * @param preferenceValue DOCUMENT ME!
	 */
	public void setParameter(TableModel tm, final String preferenceName, final String preferenceValue) {
		// preferences/properties
		if (tm == prefsTM) {
			prefsTM.setProperty(preferenceName, preferenceValue);
			props.setProperty(preferenceName, preferenceValue);
		}

		refresh();

		// reset state of Modify and Delete buttons to inactive
		// since update of parameter will clear any selections
		modifyPropBtn.setEnabled(false);
		deletePropBtn.setEnabled(false);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void refresh() {
		// refresh the view
		prefsTable.setModel(prefsTM);

		prefsTable.clearSelection();
		prefsTable.revalidate();
		prefsTable.repaint();
	}

	private void initButtonPane() {
		propBtnPane.add(addPropBtn);
		propBtnPane.add(modifyPropBtn);
		propBtnPane.add(deletePropBtn);

		okButtonPane.add(okButton);
		okButtonPane.add(cancelButton);

		modifyPropBtn.setEnabled(false);
		deletePropBtn.setEnabled(false);
		addPropBtn.addActionListener(new AddPropertyListener(this));
		modifyPropBtn.addActionListener(new ModifyPropertyListener(this));
		deletePropBtn.addActionListener(new DeletePropertyListener(this));
		okButton.addActionListener(new OkButtonListener(this));
		cancelButton.addActionListener(new CancelButtonListener(this));
		saveVizmapBtn.addItemListener(new CheckBoxListener());
		saveCyPropsBtn.addItemListener(new CheckBoxListener());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PreferenceTableModel getPTM() {
		return prefsTM;
	}

	private void initTable() {
		prefsTM = new PreferenceTableModel(props);

		prefsTable.setAutoCreateColumnsFromModel(false);
		prefsTable.setRowSelectionAllowed(true);
		lsm = prefsTable.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsm.addListSelectionListener(new TableListener(this, lsm));

		prefsTable.setModel(prefsTM);

		for (int i = 0; i < PreferenceTableModel.columnHeader.length; i++) {
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(PreferenceTableModel.alignment[i]);

			TableColumn Column = new TableColumn(i, PreferenceTableModel.columnWidth[i], renderer,
			                                     null);
			Column.setIdentifier(PreferenceTableModel.columnHeader[i]);
			prefsTable.addColumn(Column);
		}
	}

	/**
	 * Creates a new PreferencesDialog object.
	 *
	 * @param owner  DOCUMENT ME!
	 */
	public PreferencesDialogImpl(Frame owner,CyOperatingContext context, CyEventHelper eh) {
		super(owner);
		this.context = context;
		this.props = context.getProperties();
		this.eh = eh;

		initButtonPane();
		initTable();

		try {
			prefPopupInit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setTitle("Cytoscape Preferences Editor");
		pack();
		// set location relative to owner/parent
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}

	private void prefPopupInit() throws Exception {

		java.awt.GridBagConstraints gridBagConstraints;

		JPanel outerPanel = new JPanel(new java.awt.GridBagLayout());
		
		JPanel propsTablePanel = new JPanel(new java.awt.GridBagLayout());
		propsTablePanel.setBorder(BorderFactory.createTitledBorder("Properties"));
		
		propsTablePane.setBorder(BorderFactory.createEmptyBorder(2, 9, 4, 9));
		propsTablePane.getViewport().add(prefsTable, null);
		prefsTable.setPreferredScrollableViewportSize(new Dimension(400, 200));
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        propsTablePanel.add(propsTablePane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        propsTablePanel.add(propBtnPane, gridBagConstraints);
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        outerPanel.add(propsTablePanel, gridBagConstraints);
		

		JTextArea textArea = new JTextArea("NOTE: Changes to these properties are used in the current session ONLY unless otherwise specified below.");

		textArea.setBackground(outerPanel.getBackground());
		textArea.setEditable(false);
		textArea.setDragEnabled(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        outerPanel.add(textArea, gridBagConstraints);
		
		Box vizmapBox = Box.createVerticalBox();
		vizmapBox.setBorder(BorderFactory.createTitledBorder("Default Visual Styles"));
		vizmapText.setBackground(outerPanel.getBackground());
		vizmapText.setEditable(false);
		vizmapText.setDragEnabled(false);
		vizmapText.setLineWrap(true);
		vizmapText.setWrapStyleWord(true);
		vizmapBox.add(vizmapText);
		vizmapBox.add(Box.createVerticalStrut(5));
		vizmapPane.add(saveVizmapBtn);
		vizmapBox.add(vizmapPane);

		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        outerPanel.add(vizmapBox, gridBagConstraints);
		
		Box cyPropsBox = Box.createVerticalBox();
		cyPropsBox.setBorder(BorderFactory.createTitledBorder("Default Cytoscape Properties"));
		cyPropsText.setBackground(outerPanel.getBackground());
		cyPropsText.setEditable(false);
		cyPropsText.setDragEnabled(false);
		cyPropsText.setLineWrap(true);
		cyPropsText.setWrapStyleWord(true);
		cyPropsBox.add(cyPropsText);
		cyPropsBox.add(Box.createVerticalStrut(5));
		cyPropsPane.add(saveCyPropsBtn);
		cyPropsBox.add(cyPropsPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        outerPanel.add(cyPropsBox, gridBagConstraints);
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        outerPanel.add(okButtonPane, gridBagConstraints);
        
		this.getContentPane().add(outerPanel, BorderLayout.CENTER);
	}

	class AddPropertyListener implements ActionListener {
		PreferencesDialogImpl callerRef = null;

		public AddPropertyListener(PreferencesDialogImpl caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			String key = JOptionPane.showInputDialog(addPropBtn, "Enter property name:",
			                                         "Add Property", JOptionPane.QUESTION_MESSAGE);

			if (key != null) {
				String value = JOptionPane.showInputDialog(addPropBtn,
				                                           "Enter value for property " + key + ":",
				                                           "Add Property Value",
				                                           JOptionPane.QUESTION_MESSAGE);

				if (value != null) {
					String[] vals = { key, value };
					prefsTM.addProperty(vals);
					refresh(); // refresh view in table
				}
			}
		}
	}

	class ModifyPropertyListener implements ActionListener {
		PreferencesDialogImpl callerRef = null;

		public ModifyPropertyListener(PreferencesDialogImpl caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < selection.length; i++) {
				String name = new String((String) (prefsTM.getValueAt(selection[i], 0)));
				String value = new String((String) (prefsTM.getValueAt(selection[i], 1)));

				PreferenceValueDialog pd = new PreferenceValueDialog(PreferencesDialogImpl.this, name,
				                                                     value, callerRef, prefsTM,
				                                                     "Modify value...");
			}
		}
	}

	class DeletePropertyListener implements ActionListener {
		PreferencesDialogImpl callerRef = null;

		public DeletePropertyListener(PreferencesDialogImpl caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < selection.length; i++) {
				String name = new String((String) (prefsTM.getValueAt(selection[i], 0)));
				prefsTM.deleteProperty(name);
			}

			refresh();
		}
	}

	class OkButtonListener implements ActionListener {
		PreferencesDialogImpl callerRef = null;

		public OkButtonListener(PreferencesDialogImpl caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			// just saving via putAll() doesn't handle deletes...
			// therefore use TableModel's putAll() into new Properties obj
			// then clear Cytoscape's properties and
			Properties newProps = new Properties();
			callerRef.prefsTM.save(newProps);
			props.clear();
			props.putAll(newProps);
			callerRef.setVisible(false);

			if (saveVizmapAsDefault) {
				// TODO: Use new VizMap
//				eh.fireSynchronousEvent( new SaveVizmapPropsEvent() {
//					public Object getSource() { return PreferencesDialogImpl.this; }
//					}, SaveVizmapPropsListener.class );
//				saveVizmapAsDefault = false;
//				saveVizmapBtn.setSelected(false);
			}

			if (saveCyPropsAsDefault) {
				try {
					File file = context.getConfigFile("cytoscape.props");
					FileOutputStream output = new FileOutputStream(file);
					props.store(output, "Cytoscape Property File");
					System.out.println("wrote Cytoscape properties file to: "
					                   + file.getAbsolutePath());
				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("Could not write cytoscape.props file!");
				}

				saveCyPropsAsDefault = false;
				saveCyPropsBtn.setSelected(false);
			}

			final Properties op = props;
			final Properties np = newProps;

			eh.fireSynchronousEvent( new PreferencesUpdatedEvent() {
				public Object getSource() { return PreferencesDialogImpl.this; }
				public Properties getOldProperties() { return op; }
				public Properties getNewProperties() { return np; }
				}, PreferencesUpdatedListener.class );
		}
	}

	class CheckBoxListener implements ItemListener {
		public CheckBoxListener() {
			super();
		}

		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (source == saveVizmapBtn)
					saveVizmapAsDefault = true;

				if (source == saveCyPropsBtn)
					saveCyPropsAsDefault = true;
			}
		}
	}

	class CancelButtonListener implements ActionListener {
		PreferencesDialogImpl callerRef = null;

		public CancelButtonListener(PreferencesDialogImpl caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
			Properties oldProps = props;
			callerRef.prefsTM.restore(oldProps);
			callerRef.setVisible(false);
		}
	}

	class TableListener implements ListSelectionListener {
		private ListSelectionModel model = null;
		private PreferencesDialogImpl motherRef = null;

		public TableListener(PreferencesDialogImpl mother, ListSelectionModel lsm) {
			motherRef = mother;
			model = lsm;
		}

		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				StringBuffer buf = new StringBuffer();
				selection = getSelectedIndices(model.getMinSelectionIndex(),
				                               model.getMaxSelectionIndex());

				if (selection.length == 0) {
				} else {
					modifyPropBtn.setEnabled(true);
					deletePropBtn.setEnabled(true);
				}
			}
		}

		protected int[] getSelectedIndices(int start, int stop) {
			if ((start == -1) || (stop == -1)) {
				return new int[0];
			}

			int[] guesses = new int[stop - start + 1];
			int index = 0;

			for (int i = start; i <= stop; i++) {
				if (model.isSelectedIndex(i)) {
					guesses[index++] = i;
				}
			}

			int[] realthing = new int[index];
			System.arraycopy(guesses, 0, realthing, 0, index);

			return realthing;
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	public void showDialog() {
		refresh();
		setVisible(true);
	}
}
