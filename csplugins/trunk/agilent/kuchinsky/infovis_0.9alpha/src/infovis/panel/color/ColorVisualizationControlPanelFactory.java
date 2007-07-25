/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import infovis.Visualization;
import infovis.visualization.ColorVisualization;
import infovis.visualization.color.OrderedColor;
import infovis.visualization.render.VisualColor;

import java.util.ArrayList;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public class ColorVisualizationControlPanelFactory {
	static ColorVisualizationControlPanelFactory instance =
		new ColorVisualizationControlPanelFactory();
	ArrayList creators = new ArrayList();

	/**
	 * Creates a new ColorVisualizationControlPanelFactory object.
	 */
	public ColorVisualizationControlPanelFactory() {
		addDefaultCreators();
	}

	/**
	 * Adds the default creators.
	 */
	protected void addDefaultCreators() {
		add(new Creator() {
			public ColorVisualizationControlPanel create(Visualization visualization) {
			        VisualColor vc = VisualColor.get(visualization);
				ColorVisualization colorVisualization =
					vc.getColorVisualization();
				if (colorVisualization == null) {
					return new DefaultColorVisualizationControlPanel(visualization);
				}
				return null;
			}
		});
		add(new Creator() {
			public ColorVisualizationControlPanel create(Visualization visualization) {
			        VisualColor vc = VisualColor.get(visualization);
				ColorVisualization colorVisualization =
					vc.getColorVisualization();
//				if (colorVisualization instanceof EqualizedOrderedColor) {
//				    return new EqualizedOrderedColorControlPanel(visualization);
//				}
				if (colorVisualization instanceof OrderedColor)
					return new OrderedColorControlPanel(visualization);
				return null;
			}
		});
	}

	public static ColorVisualizationControlPanelFactory getInstance() {
		return instance;
	}
	
	public static void setInstance(ColorVisualizationControlPanelFactory factory) {
		instance = factory;
	}

	public static ColorVisualizationControlPanel createColorVisualisationControlPanel(Visualization visualization) {
		return getInstance().create(visualization);
	}

	/**
	 * Creates a color visualization from a Visualization.
	 *
	 * @param visualization the Visualization.
	 *
	 * @return a color visualization from a Visualization.
	 */
	public ColorVisualizationControlPanel create(Visualization visualization) {
		for (int i = 0; i < creators.size(); i++) {
			ColorVisualizationControlPanel panel =
				getCreatorAt(i).create(visualization);
			if (panel != null)
				return panel;
		}
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param index DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Creator getCreatorAt(int index) {
		return (Creator) creators.get(index);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void add(Creator c) {
		creators.add(c);
	}

	public static void addColorVisualizationControlPanel(Creator c) {
		getInstance().add(c);    
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean remove(Creator c) {
		return creators.remove(c);
	}

	public interface Creator {
		ColorVisualizationControlPanel create(Visualization visualization);
	}
}
