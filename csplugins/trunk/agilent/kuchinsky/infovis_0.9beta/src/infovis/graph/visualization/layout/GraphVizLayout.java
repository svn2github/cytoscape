/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.Visualization;
import infovis.column.ShapeColumn;
import infovis.graph.io.*;
import infovis.graph.visualization.NodeLinkGraphVisualization;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.geom.*;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * Class GraphVizLayout uses AT&amp;T GraphViz programs to perform the layout.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 * @infovis.factory GraphLayoutFactory "GraphViz/twopi" twopi
 * @infovis.factory GraphLayoutFactory "GraphViz/dot" dot
 * @infovis.factory GraphLayoutFactory "GraphViz/neato" neato
 * @infovis.factory GraphLayoutFactory "GraphViz/circo" circo
 * @infovis.factory GraphLayoutFactory "GraphViz/fdp" fdp
 *  
 */
public class GraphVizLayout extends AbstractGraphLayout {
    private static Logger logger = Logger.getLogger(GraphVizLayout.class);
    protected String layoutProgram;
    protected String layoutRatio = null;
    protected boolean debugging = true;
    protected Rectangle2D.Float bbox;
    protected transient float scale;

    public GraphVizLayout() {
        this("twopi");
    }
    
    public GraphVizLayout(String layoutProgram) {
        super();
        this.layoutProgram = layoutProgram;
    }
    
    public String getName() {
        return "GraphViz/"+layoutProgram;
    }

    public void computeShapes(
            Rectangle2D bounds,
            NodeLinkGraphVisualization vis) {
        if (bbox == null) {
            super.computeShapes(bounds, vis);
            bbox = callLayoutProgram();
        }
    }

    public String getLayoutProgram() {
        return layoutProgram;
    }

    public void setLayoutProgram(String program) {
        if (!layoutProgram.equals(program)) {
            layoutProgram = program;
            invalidateVisualization();
        }
    }

    public String getLayoutRatio() {
        return layoutRatio;
    }

    public void setLayoutRatio(String string) {
        if (layoutRatio != string
                && (layoutRatio == null || !layoutRatio.equals(string))) {
            layoutRatio = string;
            invalidateVisualization();
        }
    }

    protected void computeScale(Rectangle2D bounds) {
        double sx = bounds.getWidth() / bbox.getWidth();
        double sy = bounds.getHeight() / bbox.getHeight();

        scale = (float) Math.min(sx, sy);
    }

    protected float transformX(float x) {
        return (x - bbox.x) * scale;
    }

    protected float transformY(float y) {
        return (y - bbox.y) * scale;
    }

    protected Rectangle2D.Float callLayoutProgram() {
        OutputStream out = null;
        InputStream in = null;

        ByteArrayOutputStream wout = new ByteArrayOutputStream();
        DOTGraphWriter writer = new DOTGraphWriter(wout, visualization);

        try {
            Process proc = Runtime.getRuntime().exec(layoutProgram);
            out = proc.getOutputStream();
            in = proc.getInputStream();
        } catch (Exception ex) {
            logger.error(
                    "Exception while setting up Process: "
                    + ex.getMessage()
                    + "\nInstall GraphViz from www.graphviz.org",
                    ex);
            out = null;
            in = null;
        }

        if (out == null || in == null)
            return null;
        Cursor saved = getParent().getCursor();
        getParent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {

            writer.setShapes(getShapes());
            writer.setSize(
                    new Dimension(
                    (int) getBounds().getWidth(),
                    (int) getBounds().getHeight()));
            writer.setOrientation(getOrientation());
            writer.setLayoutRatio(layoutRatio);
            if (!writer.write())
                return null;
            writer = null;
            byte[] b = wout.toByteArray();
            wout.close();
            wout = null;
            OutputStream bout;

            if (debugging) {
                bout = new FileOutputStream("debug.out");
                bout.write(b);
                bout.close();
                bout = null;
            }

            out.write(b);
            AbstractGraphReader reader = new DOTGraphReader(
                    in,
                    getName(), 
                    this);
            if (reader == null || !reader.load()) {
                logger.error("Cannot read results of the DOT program");
                return null;
            }
            ShapeColumn shapes;
            shapes = reader.getNodeShapes();
            if (shapes != null) {
                // No reason to disable the notifications on shapes since
                // the method should be called from updateShapes
                for (int i = 0; i < shapes.size(); i++) {
                    setShapeAt(i, shapes.get(i));
                }
            }
            shapes = reader.getLinkShapes();
            
            if (shapes != null) {
                ShapeColumn linkShapes = getLinkShapes();
                linkShapes.clear();
                for (int i = 0; i < shapes.size(); i++) {
                    linkShapes.setExtend(i, shapes.get(i));
                }
            }
            reader.reset();
            return reader.getBbox();
        } catch (IOException e) {
            logger.error("Problem writing/reading GraphViz stream", e);
            return null;
        } finally {
            getParent().setCursor(saved);
        }
    }

    protected void rescaleVertex(int v) {
        Rectangle2D.Float rect = getRectAt(v);
        if (rect == null) {
            return;
        }
        rect.width *= scale;
        rect.height *= scale;
        rect.x = transformX(rect.x);
        rect.y = transformY(rect.y);
    }

    protected void rescaleEdge(int e, AffineTransform a) {
        ShapeColumn linkShapes = getLinkShapes();
        if (linkShapes.isValueUndefined(e))
            return;
        GeneralPath p = (GeneralPath) linkShapes.get(e);
        p.transform(a);
    }
    
    public void invalidate(Visualization vis) {
        super.invalidate(vis);
        bbox = null;
    }
    
    public Dimension getPreferredSize(Visualization vis) {
        if (bbox == null) {
            ((NodeLinkGraphVisualization)vis).computeShapes(null);
//            recomputeSizes();
//            bbox = callLayoutProgram();
            if (bbox != null) {
                preferredSize = new Dimension((int)bbox.getWidth(), (int)bbox.getHeight());
            }
        }
        return preferredSize; 
    }
}