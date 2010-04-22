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
import java.awt.*;

public class VennFrame extends JFrame {
    private int HEIGHT = 800;
    private int WIDTH = 700;

    public VennFrame(VennDiagram vd) {
        Container con = this.getContentPane();
        con.setBackground(Color.white);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);

        VennCanvas vc = new VennCanvas(vd);
		vc.setPreferredSize(new Dimension(700,700));
        panel.add(vc);

		String[] columnNames = new String[] {"Network Intersection Name","Number of Elements" };
		Object[][] data = new Object[vd.residualLabels.length][2];
		int i = 0;
		for ( String lab : vd.residualLabels ) {
			data[i][0] = vd.residualLabels[i];
			data[i][1] = vd.counts[i+1]; // TODO see polyData and residuals in VennAnalytics about this!!!!
			i++;
		}
		JTable resultsTable = new JTable(data,columnNames);
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
}
