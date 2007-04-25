package ding.view;

import cytoscape.render.stateful.GraphLOD;

public class PrintLOD extends GraphLOD {

    public byte renderEdges(int visibleNodeCount,
        int totalNodeCount, int totalEdgeCount) {
        return 0;
    }

    public boolean detail(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean nodeBorders(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean nodeLabels(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean customGraphics(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean edgeArrows(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean dashedEdges(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean edgeAnchors(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean edgeLabels(int renderNodeCount,
        int renderEdgeCount) {
        return true;
    }

    public boolean textAsShape(int renderNodeCount,
        int renderEdgeCount) {
        return exportTextAsShape;
    }
    
    private boolean exportTextAsShape = true;
    
    public void setPrintingTextAsShape(boolean pExportTextAsShape) {
    	exportTextAsShape = pExportTextAsShape;
    }
}
