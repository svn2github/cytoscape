package org.mskcc.csplugins.ExpressionCorrelation;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

import javax.swing.*;

/**
 * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Elena Potylitsine
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
 * User: Elena
 * Date: Jul 7, 2004
 * Time: 4:06:15 PM
 * <br>
 * Menu Method, for the Correlate plugin
 */
public class CorrelateMenu extends CytoscapePlugin {
    /**
     * Constructor.
     */
    public CorrelateMenu() {
        //set-up menu options in plugins menu
        JMenu menu = Cytoscape.getDesktop().getCyMenus().getOperationsMenu();
        JMenuItem item;
        //submenu
        JMenu submenu = new JMenu("Expression Correlation Network");
        item = new JMenuItem("Construct Correlation Network");
        item.addActionListener(new CorrelateMenuListener());
        submenu.add(item);
        JMenu subsubmenu = new JMenu("Advanced Options");
        item = new JMenuItem("Condition Network: Preview Histogram");
        item.addActionListener(new CorrelateMenuListener());
        subsubmenu.add(item);
        item = new JMenuItem("Condition Network: Using Defaults");
        item.addActionListener(new CorrelateMenuListener());
        subsubmenu.add(item);
        submenu.add(subsubmenu);
        item = new JMenuItem("Gene Network: Preview Histogram");
        item.addActionListener(new CorrelateMenuListener());
        subsubmenu.add(item);
        submenu.add(subsubmenu);
        item = new JMenuItem("Gene Network: Using Defaults");
        item.addActionListener(new CorrelateMenuListener());
        subsubmenu.add(item);
        //About box
        item = new JMenuItem("Help");
        item.addActionListener(new ExpressionCorrelationHelpAction());
        submenu.add(item);
        menu.add(submenu);
    }

    /**
     * Describes the plug in.
     *
     * @return short plug in description.
     */
    public String describe() {
        return new String("Calculates a Similarity Network from the Expression Matrix");
    }
}
