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

import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;

import cytoscape.Cytoscape;

import cytoscape.visual.LabelPosition;

import cytoscape.visual.ui.PopupLabelPositionChooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


/**
 *
 */
public class CyLabelPositionPropertyEditor extends com.l2fprod.common.beans.editor.AbstractPropertyEditor {
	private LabelPositionCellRenderer label;
	private JButton button;
	private LabelPosition position;

	/**
	 * Creates a new CyLabelPositionLabelEditor object.
	 */
	public CyLabelPositionPropertyEditor() {
		editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		((JPanel) editor).add("*", label = new LabelPositionCellRenderer());
		label.setOpaque(false);
		((JPanel) editor).add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editLabelPosition();
				}
			});
		((JPanel) editor).add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.setText("X");
		button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LabelPosition old = position;
					label.setValue(null);
					position = null;
					firePropertyChange(old, null);
				}
			});
		((JPanel) editor).setOpaque(false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getValue() {
		return position;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
	public void setValue(Object value) {
		position = (LabelPosition) value;
		label.setValue(value);
	}

	protected void editLabelPosition() {
		final LabelPosition newVal = PopupLabelPositionChooser.showDialog(Cytoscape.getDesktop(),
		                                                                  position);

		if (newVal != null) {
			final LabelPosition old = position;

			setValue(newVal);
			firePropertyChange(old, newVal);
		}
	}
}
