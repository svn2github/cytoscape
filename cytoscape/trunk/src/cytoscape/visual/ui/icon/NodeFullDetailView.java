package cytoscape.visual.ui.icon;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.view.CyNetworkView;

import cytoscape.visual.VisualPropertyType;

import ding.view.DGraphView;
import ding.view.DingCanvas;

import giny.view.GraphView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseListener;
import java.awt.image.VolatileImage;

import java.util.Map;

import javax.swing.JPanel;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class NodeFullDetailView extends JPanel {
    private static final int PADDING = 20;
    private Shape objectShape;
    private DingCanvas dc;
    private VolatileImage image;
    DGraphView newView;
    private CyNetworkView view;
    private CyNetwork net;
    private String originalId = Cytoscape.getCurrentNetwork()
                                         .getIdentifier();
    private Map<VisualPropertyType, Object> appearenceMap;

    /*
     * Dummy graph component
     */
    private static final CyNode source;
    private static final CyNode target;
    private static final CyEdge edge;
    private Component canvas;

    static {
        source = Cytoscape.getCyNode("Source");
        target = Cytoscape.getCyNode("Target");
        edge = Cytoscape.getCyEdge(
                source.getIdentifier(),
                "Edge",
                target.getIdentifier(),
                "Interaction");
    }

    //	public static Component getCurrent() {
    /**
     * Creates a new NodeFullDetailView object.
     */
    public NodeFullDetailView() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Component getCanvas() {
        return canvas;
    }

    /**
     * Creates a new NodeFullDetailView object.
     *
     * @param appearenceMap DOCUMENT ME!
     * @param shape DOCUMENT ME!
     */
    public NodeFullDetailView(
        final Map<VisualPropertyType, Object> appearenceMap, Shape shape) {
        this.objectShape = shape;
        this.appearenceMap = appearenceMap;

        this.setLayout(null);
    }

    /**
     * DOCUMENT ME!
     */
    public void createTempNetwork() {
        String title = Cytoscape.getCurrentNetwork()
                                .getTitle();
        String id = Cytoscape.getCurrentNetwork()
                             .getIdentifier();
        System.out.println("Current id: " + title);
        net = Cytoscape.createNetwork("Default Appearence");
        net.addNode(source);
        net.addNode(target);
        net.addEdge(edge);
        view = Cytoscape.createNetworkView(net);
        view.getNodeView(source)
            .setOffset(0, 0);
        view.getNodeView(target)
            .setOffset(150, 10);

        System.out.println("Focused: " +
            Cytoscape.getCurrentNetworkView().getTitle());
        System.out.println("Focused2: " +
            Cytoscape.getCurrentNetworkView().getTitle());
    }

    /**
     * DOCUMENT ME!
     */
    public void clean() {
        Cytoscape.destroyNetwork(net);
        net = null;
        canvas = null;
    }

    /**
     * DOCUMENT ME!
     */
    public void createView() {
        if (view != null) {
            final Dimension panelSize = this.getSize();
            ((DGraphView) view).getCanvas()
             .setSize(
                new Dimension((int) panelSize.getWidth() - PADDING,
                    (int) panelSize.getHeight() - PADDING));
            view.fitContent();
            canvas = (view.getComponent());

            for (MouseListener listener : canvas.getMouseListeners())
                canvas.removeMouseListener(listener);

            this.removeAll();
            this.add(canvas);

            canvas.setLocation(PADDING / 2, PADDING / 2);
            Cytoscape.getVisualMappingManager()
                     .applyAppearances();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphView getView() {
        return view;
    }

    /**
     * Copy constructor
     *
     * @param panel
     */
    public NodeFullDetailView(NodeFullDetailView panel) {
        this.appearenceMap = panel.getAppearenceMap();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map<VisualPropertyType, Object> getAppearenceMap() {
        return appearenceMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param appearenceMap DOCUMENT ME!
     */
    public void setAppearence(
        final Map<VisualPropertyType, Object> appearenceMap) {
        this.appearenceMap = appearenceMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param shape DOCUMENT ME!
     */
    public void setShape(final Shape shape) {
        this.objectShape = shape;
    }

    //	public void paintComponent(Graphics g) {
    //		final Graphics2D g2d = (Graphics2D) g;
    //		// AA on
    //		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    //				RenderingHints.VALUE_ANTIALIAS_ON);
    //	}

    // public void paintComponent(Graphics g) {
    //
    // if (objectShape == null || appearenceMap == null) {
    // return;
    // }
    //
    // final Graphics2D g2d = (Graphics2D) g;
    //
    // final double w = (Double) appearenceMap.get(NODE_WIDTH);
    // final double h = (Double) appearenceMap.get(NODE_HEIGHT);
    //
    // Shape transShape = objectShape;
    //
    // final AffineTransform af = new AffineTransform();
    //
    // // AA on
    // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_ON);
    //
    // g2d.setColor((Color) appearenceMap.get(NODE_FILL_COLOR));
    //
    // // Bound of this panel.
    // Rectangle2D bound = this.getBounds().getBounds2D();
    // Rectangle2D shapeBound = transShape.getBounds2D();
    //
    // System.out.println("Given Size (w, h): " + w + ", " + h);
    // af.setToScale(w / transShape.getBounds2D().getWidth(), h
    // / transShape.getBounds2D().getHeight());
    // transShape = af.createTransformedShape(transShape);
    // System.out.println("==== TransShape : "
    // + transShape.getBounds().getBounds2D());
    //
    // af.setToTranslation(
    // bound.getMaxX()/2 - shapeBound.getWidth() / 2,
    // bound.getMaxY()/2 - shapeBound.getHeight() / 2 - 5);
    //		
    // transShape = af.createTransformedShape(transShape);
    //
    // g2d.fill(transShape);
    //
    // // Draw border line
    // g2d.setColor((Color) appearenceMap.get(NODE_BORDER_COLOR));
    // g2d.setStroke(new BasicStroke((Float) appearenceMap
    // .get(NODE_LINE_WIDTH)));
    // g2d.draw(transShape);
    //
    // g2d.setColor((Color) appearenceMap.get(NODE_LABEL_COLOR));
    // g2d.setFont((Font)appearenceMap.get(NODE_FONT_FACE));
    //		
    //		
    // /*
    // * Draw label
    // */
    // String label = "<Label>";
    // int labelWidth = SwingUtilities.computeStringWidth(
    // g2d.getFontMetrics(), label);
    // g2d.drawString(label, (int) transShape.getBounds2D().getMinX()
    // - labelWidth / 2, (int) transShape.getBounds2D().getMinY() - 6);
    //
    // }
}
