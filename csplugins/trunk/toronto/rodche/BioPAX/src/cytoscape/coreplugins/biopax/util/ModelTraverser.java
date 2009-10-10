package cytoscape.coreplugins.biopax.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.biopax.paxtools.controller.EditorMap;
import org.biopax.paxtools.controller.ObjectPropertyEditor;
import org.biopax.paxtools.controller.PropertyEditor;
import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.Model;

/**
 * This is a BioPAX utility class that helps walking both object- and data 
 * properties, applying some logic to the property values, and tracing its path
 * (e.g., for debugging/loogging and preventing loops). 
 * 
 * @author Igor Rodchenkov (re-written Emek Demir's Traverser/Visitor of PaxTools)
 */
public abstract class ModelTraverser {
	protected Model model;
	protected BioPAXElement start;
	protected String path = "";
	protected final Set<BioPAXElement> visited;
	private final EditorMap editorMap;
		
	public ModelTraverser(EditorMap editorMap, Model model, BioPAXElement start) {
		this.editorMap = editorMap;
		this.start = start;
		this.model = model;
		visited = new HashSet<BioPAXElement>();
	}

	public ModelTraverser(EditorMap editorMap, Model model) {
		this(editorMap, model, null);
	}
	
	/**
	 * Implement the logic here: 
	 * e.g., logging, mapping, going into sub-properties, etc.
	 * 
	 * @param model
	 * @param parent parent BioPAX element
	 * @param property BioPAX property name
	 * @param value property value
	 */
	protected abstract void visit(final BioPAXElement parent, PropertyEditor editor, final Object value);
		

	/**
	 * A method to do something about the property values.
	 * 
	 * @param parent BioPAX element
	 * @param editor property editor
	 */
	public void visit(final BioPAXElement element, final PropertyEditor editor) {
		if(element == null) { return; }
		
		if (visited.contains(element)) {
			return;
		} else {
			visited.add(element);
		}
		
		Collection<Object> valueSet = new HashSet<Object>();
		
		if (editor instanceof ObjectPropertyEditor && editor.isMultipleCardinality()) {
			valueSet = (Collection<Object>) editor.getValueFromBean(element);
		} else {
			Object value = editor.getValueFromBean(element);
			valueSet.add(value);
		}
		
		for (Object value : valueSet) {
			if (value != null) {
				String oldPath = path; // save the current path
				path += "." + editor.getProperty() + "=" + value.toString();
				
				// the concrete method does the actual job!
				visit(element, editor, value); 
				
				path = oldPath; // reset the previous path
			}
		}
	}
		
	/**
	 * Traversing the BioPAX model,
	 * starting from the specified at constructor element.
	 */
	public void run() {
		if(model == null || start==null) return;
		
		visited.clear();
		path = "";
		traverse(model, start);
	}

	
	/**
	 * Traversing the BioPAX model,
	 * starting from each element in the set.
	 * 
	 * @param topElements Collection of BioPAX elements
	 */
	public void run(Collection<? extends BioPAXElement> topElements) {
		if(model == null || topElements.isEmpty()) return;
		
		visited.clear();
		path = "";
		for(BioPAXElement el: topElements) {
			traverse(model, el);
		}
	}
	
	
	protected void traverse(Model model, BioPAXElement element) {
		if (element == null) { return; }
		
		try {
			Set<PropertyEditor> editors = editorMap.getEditorsOf(element);
			for (PropertyEditor editor : editors) {
				visit(element, editor);
			}
		} catch (NullPointerException e) {
			//log.warn("no editors? ", e);
		}
	}	
		
}
