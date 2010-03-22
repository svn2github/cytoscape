package cytoscape.visual.properties;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.CustomGraphicsPool;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.customgraphic.NullCustomGraphics;
import cytoscape.visual.ui.icon.NodeIcon;
import cytoscape.visual.ui.icon.VisualPropertyIcon;
import ding.view.DNodeView;

public class NodeCustomGraphicsProp extends AbstractVisualProperty {

	@Override
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon() {

			private static final long serialVersionUID = 403672612403499816L;
			private static final int ICON_SIZE = 128;
			
			public void paintIcon(Component c, Graphics g, int x, int y) {

				super.setColor(new Color(10, 10, 10, 25));
				g2d = (Graphics2D) g;
				// AA on
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


				this.setIconHeight(ICON_SIZE+6);
				
				if (value == null || value instanceof CyCustomGraphics<?> == false) {
					drawDefaultIcon(c);
				} else {
					final CyCustomGraphics<?> cg = (CyCustomGraphics<?>) value;
					
					Image originalImg = cg.getImage();
					if(originalImg == null)
						drawDefaultIcon(c);
					else {
						scaleImage(originalImg);
					}
				}
				
			}
			
			private void drawDefaultIcon(Component c) {
				g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawString("?", c.getX() + 7,
						(int) ((c.getHeight() / 2) + 7));
				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
			
			private void scaleImage(final Image originalImg) {
				final float originalW = originalImg.getWidth(null);
				final float originalH = originalImg.getHeight(null);
				
				float ratio = 1;
				int shorterDim = 0;
				
				Image scaledImg;
				if(originalW > originalH) {
					ratio = originalH/originalW;
					shorterDim = (int) (ICON_SIZE*ratio);
					scaledImg = originalImg.getScaledInstance(ICON_SIZE, shorterDim, Image.SCALE_AREA_AVERAGING);
					g2d.drawImage(scaledImg, 5, 3+(ICON_SIZE - shorterDim)/2, null);
				} else {
					ratio = originalW/originalH;
					shorterDim = (int) (ICON_SIZE*ratio);
					scaledImg = originalImg.getScaledInstance(shorterDim, ICON_SIZE, Image.SCALE_AREA_AVERAGING);
					g2d.drawImage(scaledImg, 5, 3, null);
				}
				
			}
			
		};

		//icon.setBottomPadding(-2);

		return icon;
	}

	@Override
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_CUSTOM_GRAPHICS;
	}

	public void applyToNodeView(NodeView nv, Object o,
			VisualPropertyDependency dep) {
		if (nv == null)
			return;

		// Remove all
		if (nv instanceof DNodeView) {
			final DNodeView dv = (DNodeView) nv;
			while (dv.getNumCustomGraphics() != 0) {
				CustomGraphic custom = dv.customGraphicIterator().next();
				dv.removeCustomGraphic(custom);
			}
		}

		if ((o == null) || !(o instanceof CyCustomGraphics<?>)
				|| o instanceof NullCustomGraphics)
			return;

		System.out.println("####### Custom apply: " + o);

		final CyCustomGraphics<?> graphics = (CyCustomGraphics<?>) o;
		Collection<?> graphicsList = graphics.getCustomGraphics();

		if (nv instanceof DNodeView) {
			final DNodeView dv = (DNodeView) nv;
			while (dv.getNumCustomGraphics() != 0) {
				CustomGraphic custom = dv.customGraphicIterator().next();
				dv.removeCustomGraphic(custom);
			}

			if (graphicsList == null || graphicsList.size() == 0)
				return;

			for (Object cg : graphicsList) {
				if (cg instanceof CustomGraphic)
					dv.addCustomGraphic((CustomGraphic) cg);
			}
		}

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return def;
	}

	public Map<Object, Icon> getIconSet() {
		final Map<Object, Icon> customGraphicsIcons = new HashMap<Object, Icon>();

		for (CyCustomGraphics<?> graphics : CustomGraphicsPool.getAll()) {
			VisualPropertyIcon icon = (VisualPropertyIcon) getIcon(graphics);
			icon.setName(graphics.getDisplayName());
			customGraphicsIcons
					.put(graphics, icon);
		}

		return customGraphicsIcons;
	}

	private final CyCustomGraphics<CustomGraphic> def = new NullCustomGraphics();

}
