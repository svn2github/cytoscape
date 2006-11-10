package cytoscape.data.servers.ui;

import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.EDGE_ATTR_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.INTERACTION_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.SOURCE_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.TARGET_COLOR;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.ITEM_FONT_LARGE;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.LABEL_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogFontTheme.LABEL_ITALIC_FONT;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.CAUTION_ICON;
import static cytoscape.data.servers.ui.enums.ImportDialogIconSets.INTERACTION_ICON;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * GUI Component for specify options for network table import.<br>
 * 
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 */
public class NetworkImportOptionsPanel extends JPanel {

//	/**
//	 * For importing network files in text table/Excel format.
//	 */
//	public enum NetworkImportTemplates {
//		DEFAULT(
//				"Network Table with Interactions (Source, Target, Interaction, Edge Attribute 1, Edge Attribute 2, ...",
//				0, 1, 2), NO_INTERACTION(
//				"Network Table without Interactions (Source, Target, Edge Attribute 1, Edge Attribute 2, ...",
//				0, 1, -1), EXT_SIF(
//				"SIF-like format (Source, Interaction, Target, Edge Attribute 1, Edge Attribute 2, ...",
//				0, 2, 1),
//		// IMEX("IMEx Data Submission Format", -1, -1, -1),
//		OTHER("Other", 0, 1, 2);
//
//		private final String value;
//		private final int sourceCol;
//		private final int targetCol;
//		private final int interactionCol;
//
//		private NetworkImportTemplates(String value, int source, int target,
//				int interaction) {
//			this.value = value;
//			this.sourceCol = source;
//			this.targetCol = target;
//			this.interactionCol = interaction;
//		}
//
//		public String toString() {
//			return value;
//		}
//
//		public int getSource() {
//			return sourceCol;
//		}
//
//		public int getTarget() {
//			return targetCol;
//		}
//
//		public int getInteraction() {
//			return interactionCol;
//		}
//	}

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public NetworkImportOptionsPanel() {
		initComponents();

		initializeUIStates();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	private void initComponents() {

		advancedOptionPanel = new javax.swing.JPanel();
		sourceLabel = new javax.swing.JLabel();
		sourceComboBox = new javax.swing.JComboBox();
		interactionLabel = new javax.swing.JLabel();
		interactionComboBox = new javax.swing.JComboBox();
		targetLabel = new javax.swing.JLabel();
		targetComboBox = new javax.swing.JComboBox();
		iconLabel1 = new javax.swing.JLabel();
		iconLabel2 = new javax.swing.JLabel();
		edgeAttributesLabel = new javax.swing.JLabel();
		
		setBorder(javax.swing.BorderFactory
				.createTitledBorder("Network Import Options"));

//		formatComboBox.setModel(new javax.swing.DefaultComboBoxModel(
//				NetworkImportTemplates.values()));
//		formatComboBox.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				formatComboBoxActionPerformed(evt);
//			}
//		});

		advancedOptionPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Advanced Options"));
		sourceLabel.setText("Source");
		sourceLabel.setForeground(SOURCE_COLOR.getColor());
		sourceLabel.setFont(LABEL_FONT.getFont());

		sourceComboBox.setForeground(SOURCE_COLOR.getColor());
		sourceComboBox.setFont(ITEM_FONT_LARGE.getFont());
		sourceComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				networkColumnsComboBoxActionPerformed(evt);
			}
		});

		interactionLabel.setText("Interaction");
		interactionLabel.setForeground(INTERACTION_COLOR.getColor());
		interactionLabel.setFont(LABEL_FONT.getFont());

		interactionComboBox.setForeground(INTERACTION_COLOR.getColor());
		interactionComboBox.setFont(ITEM_FONT_LARGE.getFont());
		interactionComboBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						networkColumnsComboBoxActionPerformed(evt);
					}
				});

		targetLabel.setText("Target");
		targetLabel.setForeground(TARGET_COLOR.getColor());
		targetLabel.setFont(LABEL_FONT.getFont());

		targetComboBox.setForeground(TARGET_COLOR.getColor());
		targetComboBox.setFont(ITEM_FONT_LARGE.getFont());
		targetComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				networkColumnsComboBoxActionPerformed(evt);
			}
		});

		iconLabel1.setIcon(INTERACTION_ICON.getIcon());
		iconLabel2.setIcon(INTERACTION_ICON.getIcon());

		edgeAttributesLabel.setFont(LABEL_ITALIC_FONT.getFont());
		edgeAttributesLabel.setForeground(EDGE_ATTR_COLOR.getColor());
		edgeAttributesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		edgeAttributesLabel.setIcon(CAUTION_ICON.getIcon());
		edgeAttributesLabel
				.setText("Columns in BLUE will be loaded as EDGE ATTRIBUTES.");


		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				sourceComboBox,
																				0,
																				200,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				iconLabel1)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				sourceLabel)
																		.add(
																				206,
																				206,
																				206)))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				interactionComboBox,
																				0,
																				151,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				iconLabel2)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				interactionLabel)
																		.add(
																				130,
																				130,
																				130)))
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				targetLabel)
																		.addContainerGap(
																				182,
																				Short.MAX_VALUE))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				targetComboBox,
																				0,
																				200,
																				Short.MAX_VALUE)
																		.add(
																				22,
																				22,
																				22))))
						.add(org.jdesktop.layout.GroupLayout.TRAILING,
								edgeAttributesLabel,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								679, Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(sourceLabel)
														.add(interactionLabel)
														.add(targetLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																sourceComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(iconLabel1)
														.add(
																interactionComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(iconLabel2)
														.add(
																targetComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(edgeAttributesLabel)
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
	}// </editor-fold>


	private void networkColumnsComboBoxActionPerformed(
			java.awt.event.ActionEvent evt) {

		List<Integer> colIdx = new ArrayList<Integer>();

		colIdx.add(sourceComboBox.getSelectedIndex());
		colIdx.add(targetComboBox.getSelectedIndex());
		colIdx.add(interactionComboBox.getSelectedIndex());

		changes.firePropertyChange(
				ImportTextTableDialog.NETWORK_IMPORT_TEMPLATE_CHANGED, null,
				colIdx);
	}

	// private void interactionComboBoxActionPerformed(
	// java.awt.event.ActionEvent evt) {
	// // TODO add your handling code here:
	// }
	//
	// private void sourceComboBoxActionPerformed(java.awt.event.ActionEvent
	// evt) {
	// // TODO add your handling code here:
	// }

//	private void formatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
//		List<Integer> colIdx = new ArrayList<Integer>();
//		NetworkImportTemplates selected = (NetworkImportTemplates) formatComboBox
//				.getSelectedItem();
//
//		colIdx.add(selected.getSource());
//		colIdx.add(selected.getTarget());
//		colIdx.add(selected.getInteraction());
//
//		if (selected.getInteraction() == -1) {
//			interactionComboBox.setSelectedIndex(0);
//		} else {
//			interactionComboBox.setSelectedIndex(selected.getInteraction() + 1);
//		}
//		sourceComboBox.setSelectedIndex(selected.getSource());
//		targetComboBox.setSelectedIndex(selected.getTarget());
//
//		if (selected == NetworkImportTemplates.OTHER) {
//			sourceComboBox.setEnabled(true);
//			targetComboBox.setEnabled(true);
//			interactionComboBox.setEnabled(true);
//		} else {
//			sourceComboBox.setEnabled(false);
//			targetComboBox.setEnabled(false);
//			interactionComboBox.setEnabled(false);
//		}
//	}

	/* ============================================================================================== */

	private void initializeUIStates() {
		sourceComboBox.setEnabled(false);
		targetComboBox.setEnabled(false);
		interactionComboBox.setEnabled(false);
	}

	public void setComboBoxes(String[] columnNames) {
		/*
		 * Cleanup the combo boxes
		 */
		sourceComboBox.removeAllItems();
		targetComboBox.removeAllItems();
		interactionComboBox.removeAllItems();
		
		for (String item : columnNames) {
			sourceComboBox.addItem(item);
			targetComboBox.addItem(item);
			interactionComboBox.addItem(item);
		}

		interactionComboBox.insertItemAt("Default Interaction", 0);
		
		sourceComboBox.setEnabled(true);
		targetComboBox.setEnabled(true);
		interactionComboBox.setEnabled(true);
		
		sourceComboBox.setSelectedIndex(0);
		targetComboBox.setSelectedIndex(0);
		interactionComboBox.setSelectedIndex(0);
	}
	
	/*
	 * Get index from combo boxes. 
	 */
	public int getSourceIndex() {
		return sourceComboBox.getSelectedIndex();
	}
	
	public int getTargetIndex() {
		return targetComboBox.getSelectedIndex();
	}
	
	public int getInteractionIndex() {
		return interactionComboBox.getSelectedIndex();
	}

	// Variables declaration - do not modify
	private javax.swing.JLabel edgeAttributesLabel;
	private javax.swing.JPanel advancedOptionPanel;
	private javax.swing.JLabel iconLabel1;
	private javax.swing.JLabel iconLabel2;
	private javax.swing.JComboBox interactionComboBox;
	private javax.swing.JLabel interactionLabel;
	private javax.swing.JComboBox sourceComboBox;
	private javax.swing.JLabel sourceLabel;
	private javax.swing.JComboBox targetComboBox;
	private javax.swing.JLabel targetLabel;
	// End of variables declaration

}
