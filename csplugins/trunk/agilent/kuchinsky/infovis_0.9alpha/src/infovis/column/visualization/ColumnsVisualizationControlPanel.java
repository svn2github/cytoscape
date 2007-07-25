/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import infovis.Visualization;
import infovis.panel.ControlPanel;

import javax.swing.JComponent;

/**
 * Class ColumnsVisualizationControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * 
 * @infovis.factory ControlPanelFactory infovis.column.visualization.ColumnsVisualization
 */
public class ColumnsVisualizationControlPanel extends ControlPanel {

    public ColumnsVisualizationControlPanel(Visualization visualization) {
        super(visualization);
    }
    
    protected JComponent createDetailControlPanel() {
        return null;
    }
    protected JComponent createVisualPanel() {
        return new ColumnsVisualizationVisualPanel(
                getVisualization(), getFilter());
    }

}
