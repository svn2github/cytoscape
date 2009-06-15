package org.cytoscape.view.vizmap.gui.editor;

import java.awt.Component;

public interface ValueEditor<V> {
	
	
	/**
	 * Display the editor and get a new value.
	 * 
	 * @return
	 */
	public V showEditor(Component parent, V initialValue);
	
	public Class<? extends V> getType();

}
