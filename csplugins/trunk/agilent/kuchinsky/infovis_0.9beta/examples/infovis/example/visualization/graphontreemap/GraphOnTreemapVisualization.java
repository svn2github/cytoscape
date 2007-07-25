/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization.graphontreemap;
import infovis.*;
import infovis.column.BooleanColumn;
import infovis.column.IntColumn;
import infovis.graph.DefaultGraph;
import infovis.panel.ControlPanelFactory;
import infovis.tree.visualization.TreemapVisualization;
import infovis.visualization.LinkVisualization;
import infovis.visualization.NodeAccessor;
import infovis.visualization.linkShapers.CurvedLinkShaper;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Visualize a web site as a treemap and overlays the non-tree links as quadratic splines.
 * See HCIL-2003-32 report: "Overlaying Graph Links on Treemaps",
 * Jean-Daniel Fekete, David Wang, Niem Dang, Aleks Aris, Catherine Plaisant
 * ftp://ftp.cs.umd.edu/pub/hcil/Reports-Abstracts-Bibliography/2003-32html/2003-32.pdf 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class GraphOnTreemapVisualization
    extends TreemapVisualization
    implements NodeAccessor {
    public static final String INVERTEX_COLUMN = DefaultGraph.INVERTEX_COLUMN;
    public static final String OUTVERTEX_COLUMN = DefaultGraph.OUTVERTEX_COLUMN;
    protected Table edgeTable;
    protected boolean linksAlwaysVisible = true;
    protected LinkVisualization linkVisualization;
    protected IntColumn inColumn;
    protected IntColumn outColumn;

    static {
        ControlPanelFactory.getInstance().setDefault(
            GraphOnTreemapVisualization.class,
            GraphOnTreemapControlPanel.class);
    }

    public GraphOnTreemapVisualization(
        Tree tree,
        Table edgeTable) {
        super(tree);
        this.edgeTable = edgeTable;

        inColumn =
            IntColumn.findColumn(edgeTable, INVERTEX_COLUMN);
        outColumn =
            IntColumn.findColumn(edgeTable, OUTVERTEX_COLUMN);

        linkVisualization =
            new LinkVisualization(edgeTable, this, this);
        linkVisualization.setOrientation(ORIENTATION_INVALID);
        ChangeListener cl = new  ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BooleanColumn col = getSelection();
                linkVisualization.setShowingSelected(
                        col.getSelectedCount() != 0);
            }
        };
        getSelection().addChangeListener(cl);
        cl.stateChanged(null);
        //linkVisualization.setShowingSelected(true);
        linkVisualization.setLinkShaper(new CurvedLinkShaper());
    }

    // NodeAccessor interface    
    public int getStartNode(int link) {
        return inColumn.get(link);
    }

    public int getEndNode(int link) {
        return outColumn.get(link);
    }
    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        super.paintItems(graphics, bounds);
        linkVisualization.paint(graphics, bounds);
    }

    public void dispose() {
        super.dispose();
        linkVisualization.dispose();
    }

    public void setOrientation(short orientation) {
        if (getOrientation() == orientation) return;
        super.setOrientation(orientation);
        linkVisualization.setOrientation(orientation);
    }

    public Visualization getVisualization(int index) {
        if (index == 0)
            return linkVisualization;
        return null;
    }

    public LinkVisualization getLinkVisualization() {
        return linkVisualization;
    }
}
