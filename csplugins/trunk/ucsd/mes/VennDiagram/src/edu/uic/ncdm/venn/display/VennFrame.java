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

public class VennFrame extends JFrame {
    private int HEIGHT = 800;
    private int WIDTH = 700;
	private NumberFormat formatter = new DecimalFormat("#0.000");
	private NumberFormat intFormat = new DecimalFormat("#0");

    public VennFrame(VennDiagram vd) {
        Container con = this.getContentPane();
        con.setBackground(Color.white);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);

        VennCanvas vc = new VennCanvas(vd);
		vc.setPreferredSize(new Dimension(700,700));
        panel.add(vc);

		String[] columnNames = new String[] {"Network Intersection Name","Residuals","Size","Number of Elements" };
		Object[][] data = new Object[vd.residualLabels.length][4];
		int i = 0;
		for ( String lab : vd.residualLabels ) {
			data[i][0] = vd.residualLabels[i];
			data[i][1] = formatter.format(vd.residuals[i]);
			// TODO see polyData and residuals in VennAnalytics to understand the i+1 here!!! 
			data[i][2] = formatter.format(vd.areas[i+1]);
			data[i][3] = intFormat.format(vd.counts[i+1]); 
			i++;
		}
		JTable resultsTable = new JTable(data,columnNames);
		resultsTable.setDefaultRenderer( String.class, new WarningCellRenderer(vd) );
		resultsTable.setPreferredScrollableViewportSize(new Dimension(700, 100));
		//resultsTable.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(resultsTable);

		panel.add(scrollPane);

		con.add(panel);

        setTitle("Venn/Euler Diagram");
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);
        setVisible(true);
    }

	private class WarningCellRenderer extends JLabel
                           implements TableCellRenderer {
		private VennDiagram vd;
		public WarningCellRenderer(VennDiagram vd) {
			this.vd = vd;
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(
                            JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
			if (vd.warnings[row]) {
				setBackground(Color.red);
			}
			setToolTipText("Bad things man!");
			return this;
    	}
	}
}
