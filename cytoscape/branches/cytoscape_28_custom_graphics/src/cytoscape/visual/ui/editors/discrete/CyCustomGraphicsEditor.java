package cytoscape.visual.ui.editors.discrete;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.ui.ValueSelectDialog;

public class CyCustomGraphicsEditor extends
		com.l2fprod.common.beans.editor.AbstractPropertyEditor {
	
	private CustomGraphicsCellRenderer cellRenderer;
	private JButton button;
	private CyCustomGraphics<?> graphics;

	/**
	 * Creates a new CyLabelPositionLabelEditor object.
	 */
	public CyCustomGraphicsEditor() {
		editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		((JPanel) editor).add("*", cellRenderer = new CustomGraphicsCellRenderer());
		cellRenderer.setOpaque(false);
		
		((JPanel) editor).add(button = ComponentFactory.Helper.getFactory()
				.createMiniButton());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editLabelPosition();
			}
		});
		((JPanel) editor).add(button = ComponentFactory.Helper.getFactory()
				.createMiniButton());
		
		button.setText("X");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyCustomGraphics<?> old = graphics;
				cellRenderer.setValue(null);
				graphics = null;
				firePropertyChange(old, null);
			}
		});
		((JPanel) editor).setOpaque(false);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getValue() {
		return graphics;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param value
	 *            DOCUMENT ME!
	 */
	public void setValue(Object value) {
		graphics = (CyCustomGraphics<?>) value;
		cellRenderer.setValue(value);
	}

	protected void editLabelPosition() {
		final CyCustomGraphics<?> newVal = (CyCustomGraphics<?>) ValueSelectDialog.showDialog(VisualPropertyType.NODE_CUSTOM_GRAPHICS, null);

		if (newVal != null) {
			final CyCustomGraphics<?> old = graphics;

			setValue(newVal);
			firePropertyChange(old, newVal);
		}
	}
}