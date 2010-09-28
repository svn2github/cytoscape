package org.cytoscape.view.vizmap.gui.internal;

import org.cytoscape.view.model.NullDataType;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualLexiconNode;
import org.cytoscape.view.model.VisualProperty;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;

public class VisualPropertyTreeBuilder {
	
	
	private void createTree(final PropertySheetTableModel model, final VisualLexicon lexicon) {
		VisualProperty<NullDataType> root = lexicon.getRootVisualProperty();
		
		final DefaultProperty lexiconRoot = new DefaultProperty();
		lexiconRoot.setDisplayName(root.getDisplayName());
		
		
	}
	
	private Property buildProperty(final VisualLexiconNode node) {
		final DefaultProperty prop = new DefaultProperty();
		final VisualProperty<?> vp = node.getVisualProperty();
		
		prop.setDisplayName(vp.getDisplayName());
		prop.setName(vp.getIdString());
		
		return prop;
	}
	
	
	public void createTable(final PropertySheetTableModel model, final VisualLexicon lexicon) {
		
	}

}
