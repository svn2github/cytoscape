/*
 * VennEuler -- A Venn and Euler Diagram program.
 *
 * Copyright 2009 by Leland Wilkinson.
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1 (the "License")
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 */

package edu.uic.ncdm.venn.display;

import edu.uic.ncdm.venn.VennDiagram;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import  java.text.DecimalFormat;
import java.text.NumberFormat;

import cytoscape.Cytoscape;

public class VennFrame extends JDialog {
    private int RESIDUAL_SIZE = 100;
    private int STRESS_SIZE = 50;
    private int SIZE = 700;
	private NumberFormat floatFormat = new DecimalFormat("#0.000");
	private NumberFormat intFormat = new DecimalFormat("#0");

    public VennFrame(VennDiagram vd,boolean printIntersection) {
		super(Cytoscape.getDesktop());
        Container con = this.getContentPane();
        con.setBackground(Color.white);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);

        VennCanvas vc = new VennCanvas(vd,printIntersection);
		vc.setPreferredSize(new Dimension(SIZE,SIZE));
        panel.add(vc);

		// stress table
		String[] columnNames2 = new String[] {"Stress","Stress .01","Stress .05"};
		Object[][] data2 = new Object[1][3];
		data2[0][0] = floatFormat.format(vd.stress);
		data2[0][1] = floatFormat.format(vd.stress01);
		data2[0][2] = floatFormat.format(vd.stress05);
		JTable stressTable = new JTable(data2,columnNames2);
		stressTable.setPreferredScrollableViewportSize(new Dimension(SIZE, STRESS_SIZE));
		JScrollPane stressScroll = new JScrollPane(stressTable);
		panel.add(stressScroll);

		// residual table
		String[] columnNames = new String[] {"Network Intersection Name","Residual",
		                                     "Intersection Area","Number of Elements" };
		Object[][] data = new Object[vd.residualLabels.length][4];
		int i = 0;
		for ( String lab : vd.residualLabels ) {
			data[i][0] = vd.residualLabels[i];
			data[i][1] = floatFormat.format(vd.residuals[i]);
			// TODO see polyData and residuals in VennAnalytics to understand the i+1 here!!! 
			data[i][2] = floatFormat.format(vd.areas[i+1]);
			data[i][3] = intFormat.format(vd.counts[i+1]); 
			i++;
		}
		JTable resultsTable = new JTable(data,columnNames);
		resultsTable.setDefaultRenderer( Object.class, new WarningCellRenderer(vd) );
		resultsTable.setPreferredScrollableViewportSize(new Dimension(SIZE, RESIDUAL_SIZE));
		JScrollPane resultsScroll = new JScrollPane(resultsTable);
		panel.add(resultsScroll);

		con.add(panel);

        setTitle("Venn/Euler Diagram");
        setBounds(0, 0, SIZE, SIZE + STRESS_SIZE + RESIDUAL_SIZE);
        setResizable(false);
        setVisible(true);

		if ( vd.stress > 0.0 && vd.stress > vd.stress05 )
			JOptionPane.showMessageDialog(this, "The global stress is greater than the 5% threshold, so the results should be considered suspect!", "WARNING!", JOptionPane.WARNING_MESSAGE);	
    }

	private class WarningCellRenderer extends JLabel
                           implements TableCellRenderer {
		private VennDiagram vd;
		private Color warningColor = new Color(1.0f,0f,0f,0.25f);
		public WarningCellRenderer(VennDiagram vd) {
			this.vd = vd;
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(
                            JTable table, Object label,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
			setText((String)label);
			if (vd.warnings[row]) {
				setBackground(warningColor);
				setToolTipText("!");
			} else {
				setBackground(Color.white);
				setToolTipText("");
			}
			return this;
    	}
	}
}
