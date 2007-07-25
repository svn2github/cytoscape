/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.visualization;

import infovis.Visualization;

import javax.swing.JComponent;

/**
 * DOCUMENT ME!
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 * 
 * @infovis.factory ControlPanelFactory infovis.tree.visualization.IcicleTreeVisualization
 */
public class IcicleTreeControlPanel extends TreeControlPanel {
    /**
     * Creates a new TreemapControlPanel object.
     *
     * @param visualization DOCUMENT ME!
     */
    public IcicleTreeControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Returns the <code>TreemapVisualization</code>
     *
     * @return the <code>TreemapVisualization</code>
     */
    public IcicleTreeVisualization getIcicleTreeVisualization() {
        return (IcicleTreeVisualization) getVisualization()
            .findVisualization(
            IcicleTreeVisualization.class);
    }

    /**
     * @see infovis.panel.ControlPanel#createStdVisualPane()
     */
    protected JComponent createVisualPanel() {
        return new AdditiveTreeVisualPanel(getTreeVisualization(), getFilter());
    }
}
