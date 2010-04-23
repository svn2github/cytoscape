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
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.text.DecimalFormat;

public class VennCanvas extends JPanel {
    private BufferedImage bi;
    private int size;
    private double mins, maxs;
    private FontRenderContext frc;
	private NumberFormat intFormat;
	private VennDiagram venn;
	private boolean printIntersections;

    public VennCanvas(VennDiagram venn, boolean printIntersections) {
		this.venn = venn;
		this.printIntersections = printIntersections;
        size = 700;
        bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        frc = bi.createGraphics().getFontRenderContext();
		intFormat = new DecimalFormat("#0");
        size *= .8;
        mins = Double.POSITIVE_INFINITY;
        maxs = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < venn.centers.length; i++) {
            double margin = venn.diameters[i] / 2;
            mins = Math.min(venn.centers[i][0] - margin, mins);
            mins = Math.min(venn.centers[i][1] - margin, mins);
            maxs = Math.max(venn.centers[i][0] + margin, maxs);
            maxs = Math.max(venn.centers[i][1] + margin, maxs);
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) bi.getGraphics();
        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, bi.getHeight(), bi.getWidth());
        for (int i = 0; i < venn.centers.length; i++) {
            double xi = (venn.centers[i][0] - mins) / (maxs - mins);
            double yi = (venn.centers[i][1] - mins) / (maxs - mins);
            double pi = venn.diameters[i] / (maxs - mins);
            int pointSize = (int) (pi * size);
            int x = 50 + (int) (xi * size);
            int y = 50 + (int) (size - yi * size);
            Color color = rainbow(venn.colors[i], .4f);
            g2D.setColor(color);
            g2D.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
           	g2D.setColor(Color.black);
            double[] wh = getWidthAndHeight(venn.circleLabels[i], g2D);
			if (printIntersections) 
            	g2D.drawString(venn.circleLabels[i], x - (int) wh[0] / 2, y - (int) wh[1]);
			else
            	g2D.drawString(venn.circleLabels[i], x - (int) wh[0] / 2, y + (int) wh[1] / 2);
        }

		if ( printIntersections ) {
	        for (int i = 0; i < venn.luneCenters.length; i++) {
				if ( venn.counts[i+1] > 0 ) {
					double xi = (venn.luneCenters[i][0] - mins) / (maxs - mins);
					double yi = (venn.luneCenters[i][1] - mins) / (maxs - mins);
					int x = 50 + (int) (xi * size);
					int y = 50 + (int) (size - yi * size);
					g2D.setColor(Color.BLACK);
					String label = intFormat.format( venn.counts[i+1] ); // + " " + venn.residualLabels[i];
					double[] wh = getWidthAndHeight(label, g2D);
					g2D.drawString(label, x - (int) wh[0] / 2, y + (int) wh[1]);
				}
			}
		}

        Graphics2D gg = (Graphics2D) g;
        gg.drawImage(bi, 2, 2, this);
    }

    private static Color rainbow(double value, float transparency) {
        /* blue to red, approximately by wavelength */
        float v = (float) value * 255.f;
        float vmin = 0;
        float vmax = 255;
        float range = vmax - vmin;

        if (v < vmin + 0.25f * range)
            return new Color(0.f, 4.f * (v - vmin) / range, 1.f, transparency);
        else if (v < vmin + 0.5 * range)
            return new Color(0.f, 1.f, 1.f + 4.f * (vmin + 0.25f * range - v) / range, transparency);
        else if (v < vmin + 0.75 * range)
            return new Color(4.f * (v - vmin - 0.5f * range) / range, 1.f, 0, transparency);
        else
            return new Color(1.f, 1.f + 4.f * (vmin + 0.75f * range - v) / range, 0, transparency);
    }

    public double[] getWidthAndHeight(String s, Graphics2D g2D) {
		Font font = g2D.getFont();
        Rectangle2D bounds = font.getStringBounds(s, frc);
        double[] wh = new double[2];
        wh[0] = bounds.getWidth();
        wh[1] = .7 * bounds.getHeight();
        return wh;
    }
}
