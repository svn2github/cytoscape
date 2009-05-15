package org.cytoscape.view.vizmap.gui.editor;

public interface ValueEditor<V> {
	
	
	/**
	 * Display the editor and get a new value.
	 * 
	 * @return
	 */
	public V showEditor();

}
