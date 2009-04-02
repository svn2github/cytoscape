package org.cytoscape.view.vizmap.gui.internal.util;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

public class VizMapperUtil {

	private VisualMappingManager vmm;

	public VizMapperUtil(VisualMappingManager vmm) {
		this.vmm = vmm;
	}

	/**
	 * Get a new Visual Style name
	 * 
	 * @param s
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getStyleName(Component parentComponent, VisualStyle s) {
		String suggestedName = null;

		if (s != null)
			suggestedName = vmm.getCalculatorCatalog().checkVisualStyleName(
					s.getName());

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(parentComponent,
					"Please enter new name for the visual style.",
					"Enter Visual Style Name", JOptionPane.QUESTION_MESSAGE,
					null, null, suggestedName);

			if (ret == null)
				return null;

			String newName = vmm.getCalculatorCatalog().checkVisualStyleName(
					ret);

			if (newName.equals(ret))
				return ret;

			int alt = JOptionPane.showConfirmDialog(parentComponent,
					"Visual style with name " + ret
							+ " already exists,\nrename to " + newName
							+ " okay?", "Duplicate visual style name",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
					null);

			if (alt == JOptionPane.YES_OPTION)
				return newName;
		}
	}

}
