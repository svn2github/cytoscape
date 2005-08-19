package cytoscape.cytopanel;

import java.awt.*;
import javax.swing.Icon;

/**
 * Interface to a CytoPanel.
 *
 * @author Ben Gross.
 */
public interface ICytoPanel {

    /**
     * Adds a component to the CytoPanel.
     *
     * @param component  Component reference.
	 * @return component Component reference.
     */
    public Component add(Component component);

    /**
     * Adds a component to the CytoPanel at specified index.
     *
     * @param component Component reference.
     * @param index     Tab index.
	 * @return component Component reference.
     */
    public Component add(Component component, int index);

    /**
     * Adds a component to the CytoPanel with a specified title.
     *
     * @param title     Component title.
     * @param component Component reference.
	 * @return component Component reference.
     */
    public Component add(String title, Component component);

    /**
     * Adds a component to the CytoPanel with specified title and icon.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     */
    public void add(String title, Icon icon, Component component);

    /**
     * Adds a component to the CytoPanel with specified title, icon, and tool tip.
     *
     * @param title     Component title (can be null).
	 * @param icon      Component icon (can be null).
     * @param component Component reference.
     * @param tip       Component Tool tip text.
     */
    public void add(String title, Icon icon, Component component, String tip);

	/**
	 * Returns the currently selected component.
	 *
	 * @return component Currently selected Component reference.
	 */
	public Component getSelectedComponent();

	/**
	 * Returns the currently selected index.
	 *
	 * @return index Currently selected index.
	 */
	public int getSelectedIndex();

	/**
	 * Returns the index of the tab for the specified component.
	 *
     * @param component Component reference.
	 * @return int      Tab Index of the Component or -1 if not found.
	 */
	public int indexOfComponent(Component component);

	/**
	 * Returns the first tab index with given title.
	 *
     * @param title Tab title.
	 * @return int  Tab index with given title or -1 if not found.
	 */
	public int indexOfTab(String title);

	/**
	 * Remove specified component from the CytoPanel.
	 *
	 * @param component Component reference.
	 */
	public void remove(Component component);

	/**
	 * Removes the tab and component from the CytoPanel at the specified index.
	 *
     * @param index Tab index.
	 */
	public void remove(int index);

	/**
	 * Removes all the tabs and corresponding components from the CytoPanel.
	 */
	public void removeAll();

    /**
     * Set the state of the CytoPanel.
     *
     * @param An integer representing the CytoPanel state - see CytoPanelConstants class.
     */
    public void setState(int cytoPanelState);

    /**
     * Get the state of the CytoPanel.
     *
	 * @return An integer representing the CytoPanel state - CytoPanelConstants class.
     */
    public int getState();

	/**
	 * Add a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a ICytoPanelListener.
	 */
	public void addCytoPanelListener(ICytoPanelListener cytoPanelListener);

	/**
	 * Remove a CytoPanel listener.
	 *
	 * @param cytoPanelListener Reference to a ICytoPanelListener.
	 */
	public void removeCytoPanelListener(ICytoPanelListener cytoPanelListener);

}
