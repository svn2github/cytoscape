package org.cytoscape.phylotree.layout;

import cytoscape.layout.AbstractLayout;

public class MyLayout extends AbstractLayout {

	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
	public  String getName() {
		return "My Layout";
	}

	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
		return "My first Layout";
	}
	public void construct() {};
}
