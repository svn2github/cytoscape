
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.visual.ui.editors.discrete;

import cytoscape.logger.CyLogger;

import com.l2fprod.common.beans.editor.StringPropertyEditor;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.LookAndFeelTweaks;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JTextField;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class CyStringPropertyEditor extends StringPropertyEditor {
	private Object currentValue;
	private Object selected;
	private CyLogger logger = CyLogger.getLogger(CyStringPropertyEditor.class);

	/**
	 * Creates a new CyStringPropertyEditor object.
	 */
	public CyStringPropertyEditor() {
		editor = new JTextField();
		((JTextField) editor).setBorder(LookAndFeelTweaks.EMPTY_BORDER);

		((JTextField) editor).addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					Method getM = null;
					Object val = null;

					try {
						getM = e.getOppositeComponent().getClass().getMethod("getSelectedRow", new Class[] {});
					} catch (SecurityException e1) {
						logger.warn("Can't get 'getSelectedRow' method from text field!", e1);
					} catch (NoSuchMethodException e1) {
						logger.warn("Can't find 'getSelectedRow' method in text field!", e1);
					}

					try {
						val = getM.invoke(e.getOppositeComponent(), new Object[] {});
					} catch (Exception e1) {
						logger.warn("Can't invoke 'getSelectedRow' method of text field: "+e1.getMessage(), e1);
					}

					try {
						getM = e.getOppositeComponent().getClass()
						        .getMethod("getValueAt", new Class[] { int.class, int.class });
					} catch (SecurityException e1) {
						logger.warn("Can't get 'getValueAt' method from text field!", e1);
					} catch (NoSuchMethodException e1) {
						logger.warn("Can't find 'getValueAt' method in text field!", e1);
					}

					Object val2 = null;

					try {
						val2 = getM.invoke(e.getOppositeComponent(),
						                   new Object[] { (Integer) val, new Integer(0) });
					} catch (Exception e1) {
						logger.warn("Can't invoke 'getValueAt' method of text field: "+e1.getMessage(), e1);
					}

					selected = ((Item) val2).getProperty().getDisplayName();
					currentValue = ((JTextField) editor).getText();
				}

				public void focusLost(FocusEvent arg0) {
					firePropertyChange(selected, ((JTextField) editor).getText());
				}
			});
	}
}
