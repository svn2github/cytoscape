package org.cytoscape.view.presentation.processing.internal;

import java.util.HashMap;
import java.util.Map;

import processing.core.PFont;

public class FontManager {
	
	private final Map<String, PFont> fontMap;
	
	public FontManager() {
		this.fontMap = new HashMap<String, PFont>();
	}
	
	
	public PFont getFont(String fontName) {
		return null;
	}
	
	public PFont getFont(String fontName, boolean create) {
		return null;
	}

}
