package org.cytoscape.application.widget.vizmap.ui.editor;

import java.awt.Font;

import org.cytoscape.application.util.Cytoscape;
import org.cytoscape.application.widget.vizmap.ui.PopupFontChooser;

import com.l2fprod.common.beans.editor.FontPropertyEditor;
import com.l2fprod.common.util.ResourceManager;


public class CyFontPropertyEditor extends FontPropertyEditor {
	
	protected void selectFont() {
		ResourceManager rm = ResourceManager.all(FontPropertyEditor.class);
	    String title = rm.getString("FontPropertyEditor.title");

	    Font font = (Font) super.getValue();
	    
	    Font selectedFont = PopupFontChooser.showDialog(Cytoscape.getDesktop(), null);

	    if (selectedFont != null) {
	      Font oldFont = font;
	      Font newFont = selectedFont;
	      
	      super.setValue(newFont);
	      firePropertyChange(oldFont, newFont);
	    }
	}
}
