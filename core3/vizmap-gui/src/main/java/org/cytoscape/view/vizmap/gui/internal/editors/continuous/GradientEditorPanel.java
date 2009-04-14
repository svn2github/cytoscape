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
package org.cytoscape.view.vizmap.gui.internal.editors.continuous;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.gui.internal.CyColorChooser;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.ContinuousMappingPoint;


/**
 * Gradient editor.
 *
 * @version 0.7
 * @since Cytoscpae 2.5
 * @author kono
 */
public class GradientEditorPanel extends ContinuousMappingEditorPanel
    implements PropertyChangeListener {
	private final static long serialVersionUID = 1202339877433771L;
	// For presets
	private static final Color DEF_LOWER_COLOR = Color.BLACK;
	private static final Color DEF_UPPER_COLOR = Color.WHITE;

	/**
	 * Creates a new GradientEditorPanel object.
	 *
	 * @param type
	 *            DOCUMENT ME!
	 */
	public GradientEditorPanel(VisualProperty<Color> type) {
		super(type);
		iconPanel.setVisible(false);
		initSlider();

		belowPanel.addPropertyChangeListener(this);
		abovePanel.addPropertyChangeListener(this);
		if(mapping != null && mapping.getPointCount() == 0)
			addButtonActionPerformed(null);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 * @param title DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 */
	public static Object showDialog(final int width, final int height, final String title,
	                                VisualProperty type, Component parentComponent) {
		editor = new GradientEditorPanel(type);
		editor.setSize(new Dimension(width, height));
		editor.setTitle(title);
		editor.setAlwaysOnTop(true);
		editor.setLocationRelativeTo(parentComponent);
		editor.setVisible(true);
		editor.repaint();

		return editor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImageIcon getLegend(final int width, final int height,
	                                  final VisualProperty type) {
		editor = new GradientEditorPanel(type);

		CyGradientTrackRenderer rend = (CyGradientTrackRenderer) editor.slider.getTrackRenderer();
		rend.getRendererComponent(editor.slider);

		return rend.getLegend(width, height);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImageIcon getIcon(final int iconWidth, final int iconHeight,
	                                VisualProperty type) {
		editor = new GradientEditorPanel(type);

		CyGradientTrackRenderer rend = (CyGradientTrackRenderer) editor.slider.getTrackRenderer();
		rend.getRendererComponent(editor.slider);

		return rend.getTrackGraphicIcon(iconWidth, iconHeight);
	}

	@Override
	protected void addButtonActionPerformed(ActionEvent evt) {
		final BoundaryRangeValues lowerRange;
		
		double maxValue = EditorValueRangeTracer.getTracer().getMax(type);

		if (mapping.getPointCount() == 0) {
			double rangeValue = EditorValueRangeTracer.getTracer().getRange(type);
			double minValue = EditorValueRangeTracer.getTracer().getMin(type);
			
			final BoundaryRangeValues upperRange;
			
			slider.getModel().addThumb(10f, DEF_LOWER_COLOR);
			slider.getModel().addThumb(90f, DEF_UPPER_COLOR);

			lowerRange = new BoundaryRangeValues(below, DEF_LOWER_COLOR, DEF_LOWER_COLOR);
			upperRange = new BoundaryRangeValues(DEF_UPPER_COLOR, DEF_UPPER_COLOR, above);
//			mapping.addPoint(maxValue / 2, lowerRange);
			mapping.addPoint(rangeValue*0.1 + minValue, lowerRange);
			mapping.addPoint(rangeValue*0.9 + minValue, upperRange);
			//Cytoscape.redrawGraph(vmm.getNetworkView());

			slider.repaint();
			repaint();

			return;
		}

		// Add a new white thumb in the min.
		slider.getModel().addThumb(100f, Color.white);

		// Pick Up first point.
		final ContinuousMappingPoint previousPoint = mapping.getPoint(mapping.getPointCount() - 1);

		final BoundaryRangeValues previousRange = previousPoint.getRange();
		lowerRange = new BoundaryRangeValues(previousRange);

		lowerRange.lesserValue = slider.getModel().getSortedThumbs()
		                             .get(slider.getModel().getThumbCount() - 1);
		System.out.println("EQ color = " + lowerRange.lesserValue);
		lowerRange.equalValue = Color.white;
		lowerRange.greaterValue = previousRange.greaterValue;
		mapping.addPoint(maxValue, lowerRange);

		updateMap();

		//Cytoscape.redrawGraph(vmm.getNetworkView());

		slider.repaint();
		repaint();
	}

	@Override
	protected void deleteButtonActionPerformed(ActionEvent evt) {
		final int selectedIndex = slider.getSelectedIndex();

		if (0 <= selectedIndex) {
			slider.getModel().removeThumb(selectedIndex);
			mapping.removePoint(selectedIndex);
			updateMap();
			//mapping.fireStateChanged();

			//Cytoscape.redrawGraph(vmm.getNetworkView());
			repaint();
		}
	}

	private void setColor(final Color newColor) {
		slider.getModel().getThumbAt(slider.getSelectedIndex()).setObject(newColor);

		final ContinuousMapping cMapping = mapping;
		int selected = getSelectedPoint(slider.getSelectedIndex());

		cMapping.getPoint(selected).getRange().equalValue = newColor;

		final BoundaryRangeValues brv = new BoundaryRangeValues(cMapping.getPoint(selected)
		                                                                .getRange().lesserValue,
		                                                        newColor,
		                                                        cMapping.getPoint(selected)
		                                                                .getRange().greaterValue);

		cMapping.getPoint(selected).setRange(brv);

		int numPoints = cMapping.getAllPoints().size();

		// Update Values which are not accessible from
		// UI
		if (numPoints > 1) {
			if (selected == 0)
				brv.greaterValue = newColor;
			else if (selected == (numPoints - 1))
				brv.lesserValue = newColor;
			else {
				brv.lesserValue = newColor;
				brv.greaterValue = newColor;
			}

			//cMapping.fireStateChanged();

			//Cytoscape.redrawGraph(vmm.getNetworkView());
			slider.repaint();
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void initSlider() {
		Dimension dim = new Dimension(600, 100);
		setPreferredSize(dim);
		setSize(dim);
		setMinimumSize(new Dimension(300, 80));
		slider.updateUI();

		//		slider.setComponentPopupMenu(menu);
		slider.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
					} else {
						final JComponent selectedThumb = slider.getSelectedThumb();

						if (selectedThumb != null) {
							final Point location = selectedThumb.getLocation();
							double diff = Math.abs(location.getX() - e.getX());

							if (e.getClickCount() == 2) {
								final Color newColor = CyColorChooser.showDialog(slider,
								                                                 "Choose new color...",
								                                                 Color.white);

								if (newColor != null) {
									//Set new color
									setColor(newColor);
								}
							}
						}
					}
				}
			});

		final double actualRange = EditorValueRangeTracer.getTracer().getRange(type);
		final double minValue = EditorValueRangeTracer.getTracer().getMin(type);

		if (allPoints != null) {
			for (ContinuousMappingPoint point : allPoints) {
				BoundaryRangeValues bound = point.getRange();

				slider.getModel()
				      .addThumb(((Double) ((point.getValue() - minValue) / actualRange)).floatValue() * 100,
				                (Color) bound.equalValue);
			}

			if (allPoints.size() != 0) {
				below = (Color) allPoints.get(0).getRange().lesserValue;
				above = (Color) allPoints.get(allPoints.size() - 1).getRange().greaterValue;
			} else {
				below = Color.black;
				above = Color.white;
			}

			setSidePanelIconColor((Color) below, (Color) above);
		}

		TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

		CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(type, (Color) below,
		                                                            (Color) above,
		                                                            mapping
		                                                                                                                                                                                                                                                                                                            .getControllingAttributeName());
		//updateBelowAndAbove();
		slider.setThumbRenderer(thumbRend);
		slider.setTrackRenderer(gRend);
		slider.addMouseListener(new ThumbMouseListener());

		/*
		 * Set tooltip for the slider.
		 */
		slider.setToolTipText("Double-click handles to edit boundary colors.");
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(BelowAndAbovePanel.COLOR_CHANGED)) {
			String sourceName = ((BelowAndAbovePanel) e.getSource()).getName();

			if (sourceName.equals("abovePanel"))
				this.above = e.getNewValue();
			else
				this.below = e.getNewValue();

			final CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(type, (Color) below,
			                                                                  (Color) above,
			                                                                  mapping
			                                                                                                                                                                                                                                                                                                                                     .getControllingAttributeName());
			slider.setTrackRenderer(gRend);

			repaint();
		}
	}
}
