package csplugins.mcode.internal;

import cytoscape.CyNetwork;
import ding.view.DGraphView;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Gary Bader
 * * Date: Jun 25, 2004
 * * Time: 7:00:13 PM
 * * Description: Utilities for MCODE
 */

/**
 * Utilities for MCODE
 */
public class MCODEUtil {
	/**
	 * Converts a list of MCODE generated clusters to a list of networks that is
	 * sorted by the score of the cluster
	 * 
	 * @param clusters
	 *            List of MCODE generated clusters
	 * @return A sorted array of cluster objects based on cluster score.
	 */
	public static MCODECluster[] sortClusters(MCODECluster[] clusters) {
		Arrays.sort(clusters, new Comparator() {
			// sorting clusters by decreasing score
			public int compare(Object o1, Object o2) {
				double d1 = ((MCODECluster) o1).getClusterScore();
				double d2 = ((MCODECluster) o2).getClusterScore();
				if (d1 == d2) {
					return 0;
				} else if (d1 < d2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		return clusters;
	}
}
