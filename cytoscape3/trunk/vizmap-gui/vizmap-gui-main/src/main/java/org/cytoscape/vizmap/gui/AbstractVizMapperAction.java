package org.cytoscape.vizmap.gui;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.gui.theme.IconManager;
import org.cytoscape.vizmap.gui.util.VizMapperUtil;

public abstract class AbstractVizMapperAction extends AbstractAction implements
		VizMapperAction {

	private static final long serialVersionUID = 1499424630636172107L;
	
	protected VizMapperUtil vizMapperUtil;
	protected DefaultAppearenceBuilder defAppBldr;
	protected VisualMappingManager visualMappingManager;
	protected VizMapperMainPanel vizMapperMainPanel;
	protected IconManager iconManager;
	protected String menuLabel;
	protected String iconId;

	protected JMenuItem menuItem;

	/*
	 * Setters for DI
	 */
	public void setVizMapUtil(VizMapperUtil vizMapperUtil) {
		this.vizMapperUtil = vizMapperUtil;
	}

	public void setDefaultAppearenceBuilder(DefaultAppearenceBuilder defAppBldr) {
		this.defAppBldr = defAppBldr;
	}

	public void setVisualMappingManager(
			VisualMappingManager visualMappingManager) {
		this.visualMappingManager = visualMappingManager;
	}

	public void setVizMapperMainPanel(VizMapperMainPanel vizMapperMainPanel) {
		this.vizMapperMainPanel = vizMapperMainPanel;
	}

	public void setMenuLabel(final String menuLabel) {
		this.menuLabel = menuLabel;
	}

	public void setIconId(final String iconId) {
		this.iconId = iconId;
	}

	public void setIconManager(IconManager iconManager) {
		this.iconManager = iconManager;
	}

	public JMenuItem getMenu() {
		if (menuItem == null) {
			menuItem = new JMenuItem(menuLabel);
			menuItem.setIcon(iconManager.getIcon(iconId));
			menuItem.addActionListener(this);
		}
		return menuItem;
	}

}
