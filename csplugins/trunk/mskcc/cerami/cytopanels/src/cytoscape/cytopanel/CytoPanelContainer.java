package cytoscape.cytopanel;

import java.awt.*;

/**
 * Interface for Container of CytoPanel Objects.
 *
 * @author Ethan Cerami
 */
public interface CytoPanelContainer {
    /**
     * Gets CytoPanel at Specified Compass Direction.
     *
     * @param compassDirection SwingConstants integer value.
     * @return CytoPanel Object or null.
     */
//    CytoPanel getCytoPanel(int compassDirection);

    /**
     * Inserts CytoPanel at Specified Compass Direction.
     *
     * @param cytoPanel        CytoPanel Object.
     * @param compassDirection SwingConstants integer value.
     */
//    void insertCytoPanel(CytoPanel cytoPanel, int compassDirection);

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

    void validate();

    void pack();
}