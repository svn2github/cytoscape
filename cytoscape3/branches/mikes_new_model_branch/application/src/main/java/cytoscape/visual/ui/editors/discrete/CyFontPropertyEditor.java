package cytoscape.visual.ui.editors.discrete;

import com.l2fprod.common.beans.editor.FontPropertyEditor;
import com.l2fprod.common.util.ResourceManager;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.ui.editors.discrete.PopupFontChooser;

import java.awt.*;

public class CyFontPropertyEditor extends FontPropertyEditor {
	private final CytoscapeDesktop desk;
	public CyFontPropertyEditor(final CytoscapeDesktop desk) {
		this.desk = desk;
	}
	
	protected void selectFont() {
		ResourceManager rm = ResourceManager.all(FontPropertyEditor.class);
	    String title = rm.getString("FontPropertyEditor.title");

	    Font font = (Font) super.getValue();
	    
	    Font selectedFont = PopupFontChooser.showDialog(desk, font);

	    if (selectedFont != null) {
	      Font oldFont = font;
	      Font newFont = selectedFont;
	      
	      super.setValue(newFont);
	      firePropertyChange(oldFont, newFont);
	    }
	}
}
