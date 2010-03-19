package cytoscape.visual.ui.editors.discrete;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import cytoscape.visual.customgraphic.CustomGraphicsUtil;
import cytoscape.visual.customgraphic.CyCustomGraphics;

public class CustomGraphicsCellRenderer extends DefaultCellRenderer {
	
	private static final long serialVersionUID = 381040361846340312L;
	
	private Map<CyCustomGraphics<?>, Icon> iconMap;
	
	public CustomGraphicsCellRenderer() {
		super();
		iconMap = new HashMap<CyCustomGraphics<?>, Icon>();
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		final JLabel label = new JLabel();
		
		if (isSelected) {
			label.setBackground(table.getSelectionBackground());
			label.setForeground(table.getSelectionForeground());
		} else {
			label.setBackground(table.getBackground());
			label.setForeground(table.getForeground());
		}

		if ((value != null) && value instanceof CyCustomGraphics<?>) {
			final CyCustomGraphics<?> cg = (CyCustomGraphics<?>) value;
			final Icon icon = iconMap.get(cg);
			if(icon == null)
				label.setIcon(new ImageIcon(CustomGraphicsUtil.getResizedImage(cg.getImage(), 96, null, true)));
			else
				label.setIcon(icon);
			
			label.setVerticalAlignment(SwingConstants.CENTER);
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}

		return label;
	}
}
