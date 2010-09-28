package org.cytoscape.view.vizmap.gui.internal.editor.valueeditor;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cytoscape.view.vizmap.gui.editor.ValueEditor;

public abstract class AbstractValueEditor<V> implements ValueEditor<V> {

	protected Class<? extends V> type;
	
	protected final JOptionPane pane;
	protected JDialog editorDialog;
	
	public AbstractValueEditor(Class<? extends V> type) {
		this.type = type;
		pane = new JOptionPane();
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
	}

	public Class<? extends V> getType() {
		return type;
	}

	public abstract V showEditor(Component parent, V initialValue);

}
