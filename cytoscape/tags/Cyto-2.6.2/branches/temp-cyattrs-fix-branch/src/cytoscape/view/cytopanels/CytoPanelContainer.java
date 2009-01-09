//     
// $Id$
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Interface for Container of CytoPanel Objects.
 *
 * @author Ethan Cerami
 */
public interface CytoPanelContainer {
    /**
     * Inserts CytoPanel at Specified Compass Direction.
     *
     * @param cytoPanel        CytoPanel Object.
     * @param compassDirection SwingConstants integer value.
     */
    void insertCytoPanel(CytoPanelImp cytoPanel, int compassDirection);

    /**
     * Gets Location of Container, in screen coordinates.
     *
     * @return Point Object.
     */
    Point getLocationOnScreen();

    /**
     * Gets Bounds of Container, relative to parent component.
     *
     * @return Rectangle Object.
     */
    Rectangle getBounds();
}
