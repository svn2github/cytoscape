package cytoscape.view;

import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;
import javax.swing.event.SwingPropertyChangeSupport;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.view.cytopanels.CytoPanel;

public interface Desktop {

	/**
	 *
	 */
	public static final String NETWORK_VIEWS_SELECTED = "NETWORK_VIEWS_SELECTED";
	/**
	 *
	 */
	public static final String NETWORK_VIEW_FOCUSED = "NETWORK_VIEW_FOCUSED";
	/**
	 *
	 */
	public static final String NETWORK_VIEW_FOCUS = "NETWORK_VIEW_FOCUS";
	/**
	 *
	 */
	public static final String NETWORK_VIEW_CREATED = "NETWORK_VIEW_CREATED";
	/**
	 *
	 */
	public static final String NETWORK_VIEW_DESTROYED = "NETWORK_VIEW_DESTROYED";
	// state variables
	/**
	 *
	 */
	public static final String VISUAL_STYLE = "VISUAL_STYLE";
	/**
	 *
	 */
	public static final String VIZMAP_ENABLED = "VIZMAP_ENABLED";

	/**
	 * Sets the Status Bar Message.
	 *
	 * @param msg
	 *            Status Bar Message.
	 */
	public void setStatusBarMsg(String msg);

	/**
	 * Clears the Status Bar Message.
	 */
	public void clearStatusBar();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public NetworkPanel getNetworkPanel();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyMenus getCyMenus();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newNetwork DOCUMENT ME!
	 */
	public void setNewNetwork(CyNetwork newNetwork);

	/**
	 * @param style
	 *            the NEW VisualStyle
	 * @return the OLD VisualStyle
	 */
	public VisualStyle setVisualStyle(VisualStyle style);

	/**
	 *  TODO: We should remove one of this event!
	 *
	 * @param network_id DOCUMENT ME!
	 */
	public void setFocus(Long network_id);

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent e);

	/**
	 * Gets a cytoPanel given a Compass direction.
	 *
	 * @param compassDirection
	 *            Compass Direction (SwingConstants.{SOUTH,EAST,WEST}).
	 * @return CytoPanel The CytoPanel that lives in the region specified by
	 *         compass direction.
	 */
	public CytoPanel getCytoPanel(int compassDirection);

	/**
	 * Gets the NetworkView Manager.
	 *
	 * @return NetworkViewManager Object.
	 */
	public NetworkViewManager getNetworkViewManager();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public BirdsEyeViewHandler getBirdsEyeViewHandler();

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JFrame getJFrame();

}