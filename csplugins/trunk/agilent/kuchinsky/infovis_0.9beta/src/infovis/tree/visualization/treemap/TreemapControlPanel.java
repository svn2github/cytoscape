/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.Visualization;
import infovis.tree.visualization.TreeControlPanel;
import infovis.tree.visualization.TreemapVisualization;

import javax.swing.JComponent;

/**
 * Control panel class for TreemapVisualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 * 
 * @infovis.factory ControlPanelFactory infovis.tree.visualization.TreemapVisualization
 */
public class TreemapControlPanel extends TreeControlPanel {
    /**
     * Creates a new TreemapControlPanel object.
     *
     * @param visualization the visualization
     */
    public TreemapControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Returns the <code>TreemapVisualization</code>.
     *
     * @return the <code>TreemapVisualization</code>
     */
    public TreemapVisualization getTreemapVisualization() {
        return (TreemapVisualization) getVisualization()
            .findVisualization(
            TreemapVisualization.class);
    }

    /**
     * @see infovis.panel.ControlPanel#createStdVisualPane()
     */
    protected JComponent createVisualPanel() {
        JComponent ret =
            new TreemapVisualPanel(getVisualization(), getFilter());
//        if (visual instanceof DefaultVisualPanel) {
//            DefaultVisualPanel vis = (DefaultVisualPanel) visual;
//
//            FilteredColumnListModel sizeModel = vis.getSizeModel();
//            sizeModel
//                .setFilter(new ComposeOrFilter(
//                    sizeModel.getFilter(),
//                    new ColumnFilter() {
//                public boolean filter(Column column) {
//                    if (column instanceof NumberColumn) {
//                        NumberColumn col = (NumberColumn) column;
//
//                        return AdditiveAggregation.isAdditive(
//                            col,
//                            getTree())
//                            == AdditiveAggregation.AGGREGATE_NO;
//                    }
//                    return false;
//                }
//            }));
//        }
        return ret;
    }
}
