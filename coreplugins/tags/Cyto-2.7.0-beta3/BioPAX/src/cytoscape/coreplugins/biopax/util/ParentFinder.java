package cytoscape.coreplugins.biopax.util;

import org.biopax.paxtools.controller.AbstractTraverser;
import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.controller.PropertyEditor;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;

/**
 * 
 * 
 * @author rodch
 *
 */
public final class ParentFinder extends AbstractTraverser {
	private BioPAXElement query;
	private boolean found;
	
	public ParentFinder(EditorMap editorMap) {
		super(editorMap);
	}

	@Override
	protected void visitValue(Object value, BioPAXElement parent, Model model,
			PropertyEditor editor) {
		// skip if already found or it's not a object property
		if(!found && value instanceof BioPAXElement 
				// explicit fix for the Level2: do not follow next steps...
				&& !editor.getProperty().equals("NEXT-STEP")) {
			if(query.equals(value)) {
				found = true;
			} else {
				// continue into the value's values:
				traverse((BioPAXElement)value, model);
			}
		}
	}

	/**
	 * True if the 'parent' element is in fact contains 'child'
	 * (recursively) 
	 * 
	 * @param parent
	 * @return
	 */
	public boolean isParentChild(BioPAXElement parent, BioPAXElement child) {
		query = child;
		found = false;
		run(parent, null);
		return found;
	}
	
}