/*
 * Created on May 22, 2005
 *
 */
package cytoscape.editor.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * 
 * abstract class used by the CyNodeLabeler for setting
 * data object value
 * @author Allan Kuchinsky
 * @version 1.0
 * 
 * 
 */
abstract public class TextFieldListener implements ActionListener,
		DocumentListener {
	private JTextComponent _field;

	public void setTextField(JTextComponent field) {
		_field = field;
	}

	public void actionPerformed(ActionEvent e) {
	}

	// DocumentListener interface
	public void insertUpdate(DocumentEvent e) {
		setFieldValue();
	}

	// DocumentListener interface
	public void removeUpdate(DocumentEvent e) {
		setFieldValue();
	}

	// DocumentListener interface
	public void changedUpdate(DocumentEvent e) {
		setFieldValue();
	}

	protected void setFieldValue() {
		String field_val = _field.getText().trim();
		if (field_val != null) {
			setDataObjValue(field_val);
		}
	}

	abstract protected void setDataObjValue(String field_val);
}