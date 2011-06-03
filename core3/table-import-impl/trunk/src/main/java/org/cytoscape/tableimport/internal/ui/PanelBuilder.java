package org.cytoscape.tableimport.internal.ui;

import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogColorTheme.ONTOLOGY_COLOR;
import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogFontTheme.ITEM_FONT;
import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogFontTheme.LABEL_FONT;
import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogIconSets.LOCAL_SOURCE_ICON;
import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogIconSets.REMOTE_SOURCE_ICON;
import static org.cytoscape.tableimport.internal.ui.theme.ImportDialogIconSets.REMOTE_SOURCE_ICON_LARGE;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.jdesktop.layout.GroupLayout;

public class PanelBuilder {
	
	private static final String GENE_ASSOCIATION = "gene_association";
	private static final String DEF_ANNOTATION_ITEM = "Please select an annotation data source...";

	
	private String annotationHtml = "<html><body bgcolor=\"white\"><p><strong><font size=\"+1\" face=\"serif\"><u>%DataSourceName%</u></font></strong></p><br>"
        + "<p><em>Annotation File URL</em>: <br><font color=\"blue\">%SourceURL%</font></p><br>"
        + "<p><em>Data Format</em>: <font color=\"green\">%Format%</font></p><br>"
        + "<p><em>Other Information</em>:<br>"
        + "<table width=\"300\" border=\"0\" cellspacing=\"3\" cellpadding=\"3\">"
        + "%AttributeTable%</table></p></body></html>";
	
	/*
	 * HTML strings for tool tip text
	 */
	private String ontologyHtml = "<html><body bgcolor=\"white\"><p><strong><font size=\"+1\" face=\"serif\"><u>%DataSourceName%</u></font></strong></p><br>"
	                              + "<p><em>Data Source URL</em>: <br><font color=\"blue\">%SourceURL%</font></p><br><p><em>Description</em>:<br>"
	                              + "<table width=\"300\" border=\"0\" cellspacing=\"3\" cellpadding=\"3\"><tr>"
	                              + "<td rowspan=\"1\" colspan=\"1\">%Description%</td></tr></table></p></body></html>";


	private final ImportTablePanel panel;
	
	PanelBuilder(final ImportTablePanel panel) {
		this.panel = panel;
	}
	
	
	protected void buildPanel() {

		panel.titleIconLabel1.setIcon(REMOTE_SOURCE_ICON_LARGE.getIcon());

		panel.ontologyLabel.setFont(LABEL_FONT.getFont());
		panel.ontologyLabel.setForeground(ONTOLOGY_COLOR.getColor());
		panel.ontologyLabel.setText("Ontology");

		panel.ontologyComboBox.setFont(new java.awt.Font("SansSerif", 1, 14));
		panel.ontologyComboBox.setPreferredSize(new java.awt.Dimension(68, 25));

		final ListCellRenderer ontologyLcr = panel.ontologyComboBox.getRenderer();
		panel.ontologyComboBox.setFont(ITEM_FONT.getFont());
		panel.ontologyComboBox.setForeground(ONTOLOGY_COLOR.getColor());
		panel.ontologyComboBox.setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel ontologyItem = (JLabel) ontologyLcr.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
				String url = panel.ontologyUrlMap.get(value);

				if (isSelected) {
					ontologyItem.setBackground(list.getSelectionBackground());
					ontologyItem.setForeground(list.getSelectionForeground());
				} else {
					ontologyItem.setBackground(list.getBackground());
					ontologyItem.setForeground(list.getForeground());
				}

				if ((url != null) && url.startsWith("http://")) {
					ontologyItem.setIcon(REMOTE_SOURCE_ICON.getIcon());
				} else {
					ontologyItem.setIcon(LOCAL_SOURCE_ICON.getIcon());
				}

				// if
				// (Cytoscape.getOntologyServer().getOntologyNames().contains(value))
				// {
				// ontologyItem.setForeground(ONTOLOGY_COLOR.getColor());
				// } else {
				// ontologyItem.setForeground(NOT_LOADED_COLOR.getColor());
				// }

				return ontologyItem;
			}
		});

		panel.ontologyComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ontologyComboBoxActionPerformed(evt);
			}
		});

		panel.browseOntologyButton.setText("Browse");
		panel.browseOntologyButton.setToolTipText("Browse local ontology file");
		panel.browseOntologyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseOntologyButtonActionPerformed(evt);
			}
		});

		panel.sourceLabel.setFont(LABEL_FONT.getFont());
		panel.sourceLabel.setText("Annotation");

		panel.annotationComboBox.setName("annotationComboBox");
		panel.annotationComboBox.setFont(ITEM_FONT.getFont());
		panel.annotationComboBox.setPreferredSize(new java.awt.Dimension(68, 25));

		final ListCellRenderer lcr = panel.annotationComboBox.getRenderer();
		panel.annotationComboBox.setRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel cmp = (JLabel) lcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				String url = panel.annotationUrlMap.get(value);

				if (isSelected) {
					cmp.setBackground(list.getSelectionBackground());
					cmp.setForeground(list.getSelectionForeground());
				} else {
					cmp.setBackground(list.getBackground());
					cmp.setForeground(list.getForeground());
				}

				if (value.toString().equals(DEF_ANNOTATION_ITEM)) {
					cmp.setIcon(null);
				} else if ((url != null) && url.startsWith("http://")) {
					cmp.setIcon(REMOTE_SOURCE_ICON.getIcon());
				} else {
					cmp.setIcon(LOCAL_SOURCE_ICON.getIcon());
				}

				return cmp;
			}
		});
		
		panel.annotationComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				annotationComboBoxActionPerformed(evt);
			}
		});

		panel.browseAnnotationButton.setText("Browse");
		panel.browseAnnotationButton.setToolTipText("Browse local annotation file...");
		panel.browseAnnotationButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				browseAnnotationButtonActionPerformed(evt);
			}
		});

		GroupLayout annotationAndOntologyImportPanelLayout = new GroupLayout(panel.annotationAndOntologyImportPanel);
		panel.annotationAndOntologyImportPanel.setLayout(annotationAndOntologyImportPanelLayout);

		annotationAndOntologyImportPanelLayout.setHorizontalGroup(annotationAndOntologyImportPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						annotationAndOntologyImportPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(annotationAndOntologyImportPanelLayout.createParallelGroup(GroupLayout.LEADING)
										.add(panel.sourceLabel).add(panel.ontologyLabel))
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(annotationAndOntologyImportPanelLayout.createParallelGroup(GroupLayout.TRAILING)
										.add(panel.annotationComboBox, 0, 100, Short.MAX_VALUE)
										.add(panel.ontologyComboBox, 0, 100, Short.MAX_VALUE))
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(annotationAndOntologyImportPanelLayout.createParallelGroup(GroupLayout.LEADING)
										.add(panel.browseAnnotationButton).add(panel.browseOntologyButton)).addContainerGap()));
		annotationAndOntologyImportPanelLayout.setVerticalGroup(annotationAndOntologyImportPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						annotationAndOntologyImportPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(annotationAndOntologyImportPanelLayout
										.createParallelGroup(GroupLayout.CENTER)
										.add(panel.sourceLabel)
										.add(panel.browseAnnotationButton)
										.add(panel.annotationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
								.add(annotationAndOntologyImportPanelLayout
										.createParallelGroup(GroupLayout.CENTER)
										.add(panel.ontologyLabel)
										.add(panel.ontologyComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE).add(panel.browseOntologyButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	}
	
	private void ontologyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		panel.ontologyComboBox.setToolTipText(getOntologyTooltip());
		panel.ontologyTextField.setText(panel.ontologyComboBox.getSelectedItem().toString());
	}
	
	private String getOntologyTooltip() {
		final String key = panel.ontologyComboBox.getSelectedItem().toString();
		String tooltip = ontologyHtml.replace("%DataSourceName%", key);
		final String description = panel.ontologyDescriptionMap.get(key);

		if (description == null) {
			tooltip = tooltip.replace("%Description%", "N/A");
		} else {
			tooltip = tooltip.replace("%Description%", description);
		}

		if (panel.ontologyUrlMap.get(key) != null) {
			return tooltip.replace("%SourceURL%", panel.ontologyUrlMap.get(key));
		} else {
			return tooltip.replace("%SourceURL%", "N/A");
		}
	}
	
	private void annotationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		if (panel.annotationComboBox.getSelectedItem().toString().equals(DEF_ANNOTATION_ITEM)) {
			panel.annotationComboBox.setToolTipText(null);

			return;
		}

		panel.annotationComboBox.setToolTipText(getAnnotationTooltip());

		try {
			final String selectedSourceName = panel.annotationComboBox.getSelectedItem().toString();
			final URL sourceURL = new URL(panel.annotationUrlMap.get(selectedSourceName));
			panel.readAnnotationForPreview(sourceURL, panel.checkDelimiter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getAnnotationTooltip() {
		final String key = panel.annotationComboBox.getSelectedItem().toString();
		String tooltip = annotationHtml.replace("%DataSourceName%", key);

		if (panel.annotationUrlMap.get(key) == null) {
			return "";
		}

		tooltip = tooltip.replace("%SourceURL%", panel.annotationUrlMap.get(key));

		if (panel.annotationFormatMap.get(key) != null) {
			tooltip = tooltip.replace("%Format%", panel.annotationFormatMap.get(key));
		} else {
			String[] parts = panel.annotationUrlMap.get(key).split("/");

			if (parts[parts.length - 1].startsWith(GENE_ASSOCIATION)) {
				tooltip = tooltip.replace("%Format%", "Gene Association");
			}

			tooltip = tooltip.replace("%Format%", "General Annotation Text Table");
		}

		if (panel.annotationAttributesMap.get(key) != null) {
			StringBuffer table = new StringBuffer();
			final Map<String, String> annotations = panel.annotationAttributesMap.get(key);

			for (String anno : annotations.keySet()) {
				table.append("<tr>");
				table.append("<td><strong>" + anno + "</strong></td><td>" + annotations.get(anno)
				             + "</td>");
				table.append("</tr>");
			}

			return tooltip.replace("%AttributeTable%", table.toString());
		}

		return tooltip.replace("%AttributeTable%", "");
	}
	
	private void browseAnnotationButtonActionPerformed(java.awt.event.ActionEvent evt) {
		DataSourceSelectDialog dssd = new DataSourceSelectDialog(DataSourceSelectDialog.ANNOTATION_TYPE,
				CytoscapeServices.desktop.getJFrame(), true);
		dssd.setLocationRelativeTo(CytoscapeServices.desktop.getJFrame());
		dssd.setVisible(true);

		String key = dssd.getSourceName();

		if (key != null) {
			panel.annotationComboBox.addItem(key);
			panel.annotationUrlMap.put(key, dssd.getSourceUrlString());
			panel.annotationComboBox.setSelectedItem(key);
			panel.annotationComboBox.setToolTipText(getAnnotationTooltip());
		}
	}
	
	private void browseOntologyButtonActionPerformed(java.awt.event.ActionEvent evt) {
		DataSourceSelectDialog dssd = new DataSourceSelectDialog(DataSourceSelectDialog.ONTOLOGY_TYPE,
				CytoscapeServices.desktop.getJFrame(), true);
		dssd.setLocationRelativeTo(CytoscapeServices.desktop.getJFrame());
		dssd.setVisible(true);

		String key = dssd.getSourceName();

		if (key != null) {
			panel.ontologyComboBox.insertItemAt(key, 0);
			panel.ontologyUrlMap.put(key, dssd.getSourceUrlString());
			panel.ontologyComboBox.setSelectedItem(key);
			panel.ontologyComboBox.setToolTipText(getOntologyTooltip());
		}
	}

}
