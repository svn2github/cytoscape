package org.cytoscape.view.presentation.processing.visualproperty;

import java.awt.Component;
import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.view.presentation.processing.CyDrawableManager;
import org.cytoscape.view.presentation.processing.P5Shape;
import org.cytoscape.view.vizmap.gui.editor.ValueEditor;

public class P5ShapeValueChooser implements ValueEditor<P5Shape> {

	private final CyDrawableManager manager;
	
	public P5ShapeValueChooser(CyDrawableManager manager) {
		this.manager = manager;
	}


	public P5Shape showEditor(Component parent, P5Shape initialValue) {
		
		final List<P5Shape> optionList = manager.getP5Shapes();
		final P5Shape[] options = new P5Shape[optionList.size()];
		for(int i=0; i<optionList.size(); i++) {
			options[i] = optionList.get(i);
		}
		
		final Object val = JOptionPane.showInputDialog(parent,
                "Please select Style",
                "Select Style",
               JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
		
		return (P5Shape) val;
	}


	public Class<P5Shape> getType() {
		return P5Shape.class;
	}


}
