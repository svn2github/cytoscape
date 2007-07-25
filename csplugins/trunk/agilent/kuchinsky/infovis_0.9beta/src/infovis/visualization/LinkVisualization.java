/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Table;
import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.utils.RowFilter;
import infovis.utils.RowIterator;
import infovis.visualization.linkShapers.DefaultLinkShaper;
import infovis.visualization.magicLens.LinkExcentricItem;
import infovis.visualization.render.VisualFilter;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;

import org.apache.log4j.Logger;

/**
 * Class LinkVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class LinkVisualization extends StrokingVisualization 
    implements Layout {
    public static final String  LINK_SHAPER = "linkShaper";
    public static final String  NODE_SHAPER = "nodeShaper";

    protected Visualization     nodeVisualization;
    protected ShapeColumn       nodeShapes;
    protected LinkShaper        linkShaper;
    protected NodeAccessor      nodeAccessor;
    protected boolean           showingSelected;

    protected volatile Point2D  startPos, endPos;
    private static final Logger logger      = 
        Logger.getLogger(LinkVisualization.class);
    
    public LinkVisualization(Table table, ItemRenderer ir,
            Visualization nodeVisualization, NodeAccessor accessor) {
        super(table, ir);
        setShowExcentric(false);
        setNodeVisualization(nodeVisualization);
        setNodeAccessor(accessor);
        VisualFilter vf = VisualFilter.get(this);
        vf.setExtraFilter(new RowFilter() {
            public boolean isFiltered(int row) {
                if (showingSelected) {
                  if (isNodeSelected(nodeAccessor.getStartNode(row))
                          || isNodeSelected(nodeAccessor.getEndNode(row)))
                      return false;
                  else
                      return true;
              }
              return LinkVisualization.this.nodeVisualization.isFiltered(
                      nodeAccessor.getStartNode(row))
                   || LinkVisualization.this.nodeVisualization.isFiltered(
                              nodeAccessor.getEndNode(row));
            }
        });
    }

    public LinkVisualization(Table table,
            Visualization nodeVisualization, NodeAccessor accessor) {
        this(table, null, nodeVisualization, accessor);
    }

    public void dispose() {
        setNodeVisualization(null);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Link";
    }
    
    /**
     * {@inheritDoc}
     */
    public void invalidate(Visualization vis) {
    }
    
    public boolean isNodeSelected(int node) {
        return (!nodeVisualization.getSelection()
                .isValueUndefined(node))
                && nodeVisualization.getSelection().get(node);
    }

    public void computeShapes(Rectangle2D bounds, Visualization vis) {
        ShapeColumn sc = 
            (ShapeColumn)nodeVisualization.getVisualColumn(VISUAL_SHAPE);
        if (nodeShapes != sc) {
            if (nodeShapes != null) {
                nodeShapes.removeChangeListener(this);
            }
            nodeShapes = sc;
            if (nodeShapes != null) {
                nodeShapes.addChangeListener(this);
            } else
                return;
            getLinkShaper().init(this, nodeShapes);
        }
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int link = iter.nextRow();
            setShapeAt(link, getLinkShaper().computeLinkShape(link,
                    getNodeAccessor(), getShapeAt(link)));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (isInvalidated()) {
            computeShapes(null);
        }
        return getShapes().getBounds().getBounds().getSize();
    }

    public LabeledItem createLabelItem(int row) {
        return new LinkExcentricItem(this, row);
    }

    public void stateChanged(ChangeEvent e) {
        super.stateChanged(e);
        if (e.getSource() == nodeShapes) {
            super.invalidate();
        }
    }

    private boolean inInvalidate;
    public void invalidate() {
        if (isInvalidated()) return;
        if (inInvalidate) {
            logger.error("Cycle in LinkVisualization.invalidate()");
            return;
        }
        try {
            inInvalidate = true;
            super.invalidate();
            nodeVisualization.invalidate();
        }
        finally {
            inInvalidate = false;
        }
    }
    
    public short getOrientation() {
        return nodeVisualization.getOrientation();
    }
    
    public void setOrientation(short orientation) {
        nodeVisualization.setOrientation(orientation);
    }

    public Visualization getNodeVisualization() {
        return nodeVisualization;
    }

    public void setNodeVisualization(Visualization visualization) {
        if (nodeShapes != null) {
            nodeShapes.removeChangeListener(this);
            nodeShapes = null;
        }
        nodeVisualization = visualization;
    }

    public NodeAccessor getNodeAccessor() {
        return nodeAccessor;
    }

    public void setNodeAccessor(NodeAccessor accessor) {
        nodeAccessor = accessor;
    }

    public LinkShaper getLinkShaper() {
        if (linkShaper == null) {
            setLinkShaper(new DefaultLinkShaper());
        }
        return linkShaper;
    }

    public void setLinkShaper(LinkShaper shaper) {
        if (shaper == linkShaper)
            return;
        if (linkShaper != null) {
            linkShaper.init(null, null);
        }
        LinkShaper old = linkShaper;
        linkShaper = shaper;
        if (linkShaper != null) {
            linkShaper.init(this, nodeShapes);
        }
        firePropertyChange(LINK_SHAPER, old, shaper);
        invalidate();
    }

    public boolean isShowingSelected() {
        return showingSelected;
    }

    public void setShowingSelected(boolean b) {
        showingSelected = b;
        repaint();
    }

}