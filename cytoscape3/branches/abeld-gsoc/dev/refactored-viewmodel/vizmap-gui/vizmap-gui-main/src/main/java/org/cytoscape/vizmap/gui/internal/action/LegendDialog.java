/*
 * LegendDialog.java
 */
package org.cytoscape.vizmap.gui.internal.action;

import org.cytoscape.vizmap.EdgeAppearanceCalculator;
import org.cytoscape.vizmap.NodeAppearanceCalculator;
import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassthroughMappingCalculator;
import org.freehep.util.export.ExportDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


/**
 *
 */
public class LegendDialog extends JDialog {
	private final static long serialVersionUID = 1202339876783665L;
	private VisualStyle visualStyle;
	private JPanel jPanel1;
	private JButton jButton1;
	private JButton jButton2;
	private JScrollPane jScrollPane1;
	private Component parent;

	/**
	 * Creates a new LegendDialog object.
	 *
	 * @param parent  DOCUMENT ME!
	 * @param vs  DOCUMENT ME!
	 */
	public LegendDialog(Component parentComponent, VisualStyle vs) {
		super();
		this.setModal(true);
		this.setLocationRelativeTo(parentComponent);
		visualStyle = vs;
		this.parent = parentComponent;
		initComponents();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param visualStyle DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static JPanel generateLegendPanel(VisualStyle visualStyle) {
		final JPanel legend = new JPanel();

		final NodeAppearanceCalculator nac = visualStyle.getNodeAppearanceCalculator();
		final List<Calculator> nodeCalcs = nac.getCalculators();
		final EdgeAppearanceCalculator eac = visualStyle.getEdgeAppearanceCalculator();
		final List<Calculator> edgeCalcs = eac.getCalculators();

		ObjectMapping om;

		/*
		 * Set layout
		 */
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
		legend.setBackground(Color.white);

		legend.setBorder(new TitledBorder(new LineBorder(Color.DARK_GRAY, 2),
		                                  "Visual Legend for " + visualStyle.getName(),
		                                  TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.CENTER,
		                                  new Font("SansSerif", Font.BOLD, 16), Color.DARK_GRAY));

		for (Calculator calc : nodeCalcs) {
			// AAARGH
			if (nac.getNodeSizeLocked()) {
				if (calc.getVisualProperty() == VisualProperty.NODE_WIDTH)
					continue;
				else if (calc.getVisualProperty() == VisualProperty.NODE_HEIGHT)
					continue;
			} else {
				if (calc.getVisualProperty() == VisualProperty.NODE_SIZE)
					continue;
			}

			om = calc.getMapping(0);

			JPanel mleg = om.getLegend(calc.getVisualProperty());

			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if (om instanceof PassthroughMappingCalculator)
				legend.add(mleg, 0);
			else
				legend.add(mleg);

			// Set padding
			mleg.setBorder(new EmptyBorder(15, 30, 15, 30));
		}

		int top = legend.getComponentCount();

		for (Calculator calc : edgeCalcs) {
			om = calc.getMapping(0);

			JPanel mleg = om.getLegend(calc.getVisualProperty());

			// Add passthrough mappings to the top since they don't
			// display anything besides the title.
			if (om instanceof PassthroughMappingCalculator)
				legend.add(mleg, 0);
			else
				legend.add(mleg);

			//			 Set padding
			mleg.setBorder(new EmptyBorder(15, 30, 15, 30));
		}

		return legend;
	}

	private void initComponents() {
		this.setTitle("Visual Legend for " + visualStyle.getName());

		jPanel1 = generateLegendPanel(visualStyle);

		jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(jPanel1);

		jButton1 = new JButton();
		jButton1.setText("Export");
		jButton1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					export();
				}
			});

		jButton2 = new JButton();
		jButton2.setText("Done");
		jButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					dispose();
				}
			});

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(jButton1);
		buttonPanel.add(jButton2);

		JPanel containerPanel = new JPanel();
		containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
		containerPanel.add(jScrollPane1);
		containerPanel.add(buttonPanel);

		setContentPane(containerPanel);
		setPreferredSize(new Dimension(650, 500));
		pack();
		repaint();
	}

	private void export() {
		ExportDialog export = new ExportDialog();
		export.showExportDialog(parent, "Export legend as ...", jPanel1, "export");
		dispose();
	}
}
