package org.mskcc.csplugins.ExpressionCorrelation;

/* * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Weston Whitaker
 * * Authors: Gary Bader, Elena Potylitsine, Chris Sander, Weston Whitaker
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
 * Created by IntelliJ IDEA.
 * User: weston
 * Date: Jul 27, 2004
 * Time: 2:38:37 PM
 * *Description: Stores the cuttoffs selected between plugin executions for the same session of Cytoscape
*/

public class CorrelateCutoffStorage {
    private static final CorrelateCutoffStorage _theInstance = new CorrelateCutoffStorage();
    private static double rowPosCutoff = 0.95;
    private static double rowNegCutoff = -0.95;
    private static double colPosCutoff = 0.95;
    private static double colNegCutoff = -0.95;

    private CorrelateCutoffStorage() {
    }

    public static CorrelateCutoffStorage getInstance() {
        return _theInstance;
    }

    public double[] getCutoffs(boolean isRowNetwork) {
        double[] cutoff = new double[2];
        if (isRowNetwork) {
            cutoff[0] = rowNegCutoff;
            cutoff[1] = rowPosCutoff;
        } else {
            cutoff[0] = colNegCutoff;
            cutoff[1] = colPosCutoff;
        }
        return cutoff;
    }


    public void setCutoffs(boolean isRowNetwork, double[] cutoffs) {
        if (isRowNetwork) {
            rowNegCutoff = cutoffs[0];
            rowPosCutoff = cutoffs[1];
        } else {
            colNegCutoff = cutoffs[0];
            colPosCutoff = cutoffs[1];
        }
    }

}
