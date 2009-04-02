package org.cytoscape.view.vizmap.gui.internal.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleCatalog;

/**
 * Utilities for setting up VizMap GUI.
 * 
 * @author kono
 *
 */
public class VizMapperUtil {

	private VisualStyleCatalog vsCatalog;

	public VizMapperUtil(VisualStyleCatalog vsCatalog) {
		this.vsCatalog = vsCatalog;
	}

	/**
	 * Get a new Visual Style name
	 * 
	 * @param s
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getStyleName(Component parentComponent, VisualStyle vs) {
		String suggestedName = null;
		List<String> vsNames = getVisualStyleNames();

		if (vs != null)
			suggestedName = vs.getTitle() + " new";

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(parentComponent,
					"Please enter new name for the visual style.",
					"Enter Visual Style Name", JOptionPane.QUESTION_MESSAGE,
					null, null, suggestedName);

			if (vsNames.contains(ret) == false)
				return ret;

			JOptionPane.showMessageDialog(parentComponent,
					"Visual style with name " + ret
							+ " already exists!", "Duplicate visual style name",
					JOptionPane.WARNING_MESSAGE,
					null);
		}
	}
	
	
	private List<String> getVisualStyleNames() {
		final List<String> vsNames = new ArrayList<String>();
		
		for(VisualStyle vs: vsCatalog.listOfVisualStyles())
			vsNames.add(vs.getTitle());
		
		return vsNames;
	}

}
