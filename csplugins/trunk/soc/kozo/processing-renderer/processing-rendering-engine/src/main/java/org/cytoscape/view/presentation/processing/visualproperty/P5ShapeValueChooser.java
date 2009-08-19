package org.cytoscape.view.presentation.processing.visualproperty;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		
		final List<P5Shape> optionSet = manager.getP5Shapes();
		final P5Shape[] options = new P5Shape[optionSet.size()];
		
		Iterator<P5Shape> itr = optionSet.iterator();
		
		int i=0;
		while(itr.hasNext()) {
			options[i] = itr.next();
			i++;
		}
		
		final Object val = JOptionPane.showInputDialog(
				parent,
                "Please select Style",
                "Select Style",
               JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                null);
		
		return (P5Shape) val;
	}


	public Class<P5Shape> getType() {
		return P5Shape.class;
	}


}
