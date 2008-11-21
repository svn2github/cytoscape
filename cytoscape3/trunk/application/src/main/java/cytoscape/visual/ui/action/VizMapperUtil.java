package cytoscape.visual.ui.action;

import javax.annotation.Resource;
import javax.swing.JOptionPane;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.view.CytoscapeDesktop;

public class VizMapperUtil {

	@Resource
	VisualMappingManager vmm;

	@Resource
	CytoscapeDesktop cytoscapeDesktop;

	/**
	 * Get a new Visual Style name
	 * 
	 * @param s
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getStyleName(VisualStyle s) {
		String suggestedName = null;

		if (s != null)
			suggestedName = vmm.getCalculatorCatalog().checkVisualStyleName(
					s.getName());

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(cytoscapeDesktop,
					"Please enter new name for the visual style.",
					"Enter Visual Style Name", JOptionPane.QUESTION_MESSAGE,
					null, null, suggestedName);

			if (ret == null)
				return null;

			String newName = vmm.getCalculatorCatalog().checkVisualStyleName(
					ret);

			if (newName.equals(ret))
				return ret;

			int alt = JOptionPane.showConfirmDialog(cytoscapeDesktop,
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
