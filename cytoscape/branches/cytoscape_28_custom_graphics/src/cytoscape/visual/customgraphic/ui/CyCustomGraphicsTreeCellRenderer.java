package cytoscape.visual.customgraphic.ui;

import java.awt.Component;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import cytoscape.visual.customgraphic.CyCustomGraphics;

public class CyCustomGraphicsTreeCellRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		System.out.println("!!!!!!Tree Rendering-----> " + value.getClass());
		if(value instanceof CyCustomGraphicsTreeNode) {
			Object image = ((CyCustomGraphicsTreeNode) value).getValueAt(2);
			final CyCustomGraphics<?> cg = (CyCustomGraphics<?>) image;

			this.setText("");
			this.setIcon(new ImageIcon(cg.getImage()));
		}
			
		return this;
	}
}
