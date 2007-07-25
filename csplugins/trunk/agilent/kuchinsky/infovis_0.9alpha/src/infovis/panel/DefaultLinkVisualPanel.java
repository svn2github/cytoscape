/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.column.ColumnFilter;
import infovis.visualization.LinkVisualization;

/**
 * Class DefaultLinkVisualPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class DefaultLinkVisualPanel extends DefaultVisualPanel {

    public DefaultLinkVisualPanel(
        LinkVisualization visualization,
        ColumnFilter filter) {
        super(visualization, filter);
    }

//    protected void createAll() {
//        addColor(getVisualization());
//        addSize(getVisualization());
//        addAlpha(getVisualization());
//        addLabel(getVisualization());
//    }
    
    public static void addVisualPanelTab(
        ControlPanel controlPanel,
        LinkVisualization linkVisualization,
        ColumnFilter filter) {
        controlPanel.getTabs().insertTab(
            "Links Visual",
            null,
            new DefaultLinkVisualPanel(linkVisualization, filter),
            null,
            2);
    }
}
