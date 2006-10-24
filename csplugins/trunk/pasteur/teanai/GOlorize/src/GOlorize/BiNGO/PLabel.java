/*
 * PLabel.java
 *
 * Created on March 31, 2006, 10:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package GOlorize.BiNGO;

/**
 *
 * @author ogarcia*/
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.util.PBoundsLocator;
import edu.umd.cs.piccolox.util.PNodeLocator;


/**
 * The Label class for nodes and edges
 */
public class PLabel extends PText
    implements giny.view.Label,
        PropertyChangeListener {
    static PNodeLocator pbl;

    /**
	 * DOCUMENT ME!
	 */
    public static int CENTERED = 0;

    /**
	 * DOCUMENT ME!
	 */
    public static int NORTH = 1;

    /**
	 * DOCUMENT ME!
	 */
    public static int NORTH_EAST = 2;

    /**
	 * DOCUMENT ME!
	 */
    public static int NORTH_WEST = 3;

    /**
	 * DOCUMENT ME!
	 */
    public static int SOUTH = 4;

    /**
	 * DOCUMENT ME!
	 */
    public static int SOUTH_EAST = 5;

    /**
	 * DOCUMENT ME!
	 */
    public static int SOUTH_WEST = 6;

    /**
	 * DOCUMENT ME!
	 */
    public static int EAST = 7;

    /**
	 * DOCUMENT ME!
	 */
    public static int WEST = 8;

    // The Edge or Node to which we are bound
    PNode boundObject;

    // The direction and amount of offset from the bound Object
    Point2D offsetAmount;

    // The text displayed by this node
    String text;
    protected int labelLocation = 0;

    /**
     * Creates a new PLabel object.
     *
     * @param text DOCUMENT ME!
     * @param node DOCUMENT ME!
     */
    public PLabel(
        String text,
        PNode node) {
        super(text);
        this.text = text;
        this.boundObject = node;
        boundObject.addPropertyChangeListener(this);
    }

    /**
     * DOCUMENT ME!
     */
    public void updatePosition() {
        if (labelLocation == NORTH) {
            pbl = PBoundsLocator.createNorthLocator(boundObject);
        } else if (labelLocation == NORTH_WEST) {
            pbl = PBoundsLocator.createNorthWestLocator(boundObject);
        } else if (labelLocation == NORTH_EAST) {
            pbl = PBoundsLocator.createNorthEastLocator(boundObject);
        } else if (labelLocation == SOUTH) {
            pbl = PBoundsLocator.createSouthLocator(boundObject);
        } else if (labelLocation == SOUTH_WEST) {
            pbl = PBoundsLocator.createSouthWestLocator(boundObject);
        } else if (labelLocation == SOUTH_EAST) {
            pbl = PBoundsLocator.createSouthEastLocator(boundObject);
        } else if (labelLocation == EAST) {
            pbl = PBoundsLocator.createEastLocator(boundObject);
        } else if (labelLocation == WEST) {
            pbl = PBoundsLocator.createWestLocator(boundObject);
        } else {
            pbl = new PNodeLocator(boundObject);
        }

        setOffset(pbl.locateX() - (0.5 * getBounds()
                                             .getWidth()),
            pbl.locateY() - (0.5 * getBounds()
                                       .getHeight()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param loc DOCUMENT ME!
     */
    public void setLabelLocation(int loc) {
        labelLocation = loc;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLabelLocation() {
        return labelLocation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName()
                   .equals("identifier")) {
            setText((String) evt.getNewValue());
        }
    }

    protected void paint(PPaintContext paintContext) {
        double s = paintContext.getScale();
        if (s > .2 ) {
            super.paint(paintContext);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     */
    public void setPositionHint(int i) {
        // TODO
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     */
    public void setSize(int i) {
        //TODO 
    }
}
