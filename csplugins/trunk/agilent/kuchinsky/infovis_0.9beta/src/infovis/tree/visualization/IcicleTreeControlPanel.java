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
 * Control Panel for IcicleTreeVisualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 * 
 * @infovis.factory ControlPanelFactory infovis.tree.visualization.IcicleTreeVisualization
 */
public class IcicleTreeControlPanel extends TreeControlPanel {
    /**
     * Creates a new TreemapControlPanel object.
     *
     * @param visualization the Visualization
     */
    public IcicleTreeControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Returns the <code>IcicleTreeVisualization</code>.
     *
     * @return the <code>TreemapVisualization</code>
     */
    public IcicleTreeVisualization getIcicleTreeVisualization() {
        return (IcicleTreeVisualization) getVisualization()
            .findVisualization(
            IcicleTreeVisualization.class);
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent createVisualPanel() {
        return new AdditiveTreeVisualPanel(getTreeVisualization(), getFilter());
    }
}
