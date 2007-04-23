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
    DGraphView newView;
    private CyNetworkView view;
    private CyNetwork net;
    private Map<VisualPropertyType, Object> appearenceMap;

    
    
    private static NodeFullDetailView currentView = null;
    
    /*
     * Dummy graph component
     */
    private static final CyNode source;
    private static final CyNode target;
    private static final CyEdge edge;
    private Component canvas = null;

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
    
    public static NodeFullDetailView getCurrentView() {
    	
    	return currentView;
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

}
