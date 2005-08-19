package cytoscape.cytopanel;

import java.awt.*;
import javax.swing.Icon;

/**
 * Interface to a CytoPanel.
 *
 * @author Ben Gross
 */
public interface ICytoPanel {

    /**
     * Adds a new Component to the CytoPanel
     *
     * @param title     Copmone title.
	 * @param icon      Icon
     * @param component Component reference.
     * @param tip       Tool tip text.
     */
    public void add(String title, Icon icon, Component component, String tip);

    /**
     * Sets the state of the CytoPanel.
     *
     * @param cytoPanelState A CytoPanelConstants state.
     */
    public void setState(int cytoPanelState);

    /**
     * Gets the state of the CytoPanel.
     *
	 * @return cytoPanelState A CytoPanel Constants state
     */
    public int getState();

	/**
	 * Add a CytoPanel listener.
	 * @param cytoPanelListener Reference to a ICytoPanelListener
	 */
	public void addCytoPanelListener(ICytoPanelListener cytoPanelListener);

	/**
	 * Remove a CytoPanel listener.
	 * @param cytoPanelListener Reference to a ICytoPanelListener
	 */
	public void removeCytoPanelListener(ICytoPanelListener cytoPanelListener);

}
