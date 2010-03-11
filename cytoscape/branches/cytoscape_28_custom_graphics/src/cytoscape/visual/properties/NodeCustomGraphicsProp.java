package cytoscape.visual.properties;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CustomGraphicsPool;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.NullCustomGraphics;
import cytoscape.visual.ui.icon.NodeIcon;
import cytoscape.visual.ui.icon.VisualPropertyIcon;

public class NodeCustomGraphicsProp extends AbstractVisualProperty {

	@Override
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon() {
			
			private static final long serialVersionUID = 403672612403499816L;

			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 25));
				super.paintIcon(c, g, x, y);
				g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawString("CG", c.getX() + 7,
				               (int) ((c.getHeight() / 2) + 7));
				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
		};

		icon.setBottomPadding(-2);

		return icon;
	}

	@Override
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_CUSTOM_GRAPHICS;
	}
	
	public void applyToNodeView(NodeView nv, Object o, VisualPropertyDependency dep) {
		
		System.out.println("####### Custom apply: " + o);
		
		if ((o == null) || (nv == null) || !(o instanceof CyCustomGraphics))
			return;
		
		final CyCustomGraphics graphics = (CyCustomGraphics) o;
		graphics.applyGraphics(nv);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return def;
	}
	
	public Map<Object, Icon> getIconSet() {
		final Map<Object, Icon> customGraphicsIcons = new HashMap<Object, Icon>();

		for (String key : CustomGraphicsPool.getPool().getNames()) {
			VisualPropertyIcon icon = (VisualPropertyIcon) getIcon(null);
			icon.setName(key);
			customGraphicsIcons.put(CustomGraphicsPool.getPool().get(key), icon);
		}

		return customGraphicsIcons;
	}
	
	private final CyCustomGraphics def = new NullCustomGraphics();

}
