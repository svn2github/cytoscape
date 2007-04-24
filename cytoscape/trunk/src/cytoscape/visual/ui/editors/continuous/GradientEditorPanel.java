package cytoscape.visual.ui.editors.continuous;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.AddPointListener;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 */
public class GradientEditorPanel extends ContinuousMappingEditorPanel {
	private static JPopupMenu menu;
	private static JMenuItem deleteThumb;
	private static JMenuItem newThumb;
	
	

	static {
		menu = new JPopupMenu();
		deleteThumb = new JMenuItem("Delete Selected Knob");
		newThumb = new JMenuItem("Add New Knob Here...");

		menu.add(newThumb);
		menu.add(deleteThumb);
	}

	/**
	 * Creates a new GradientEditorPanel object.
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 */
	public GradientEditorPanel(VisualPropertyType type) {
		super(type);
		iconPanel.setVisible(false);
		setSlider();
		//initButtons();
	}

	@Override
	protected void addButtonActionPerformed(ActionEvent evt) {
		// Add a new white thumb in the min.
		slider.getModel().addThumb(100f, Color.white);
		
		// Update continuous mapping
		final Double newVal = maxValue;
		
		// Pick Up first point.
		final ContinuousMappingPoint previousPoint = mapping.getPoint(mapping.getPointCount() - 1);
		
       final BoundaryRangeValues previousRange = previousPoint.getRange();
		final BoundaryRangeValues newRange = new BoundaryRangeValues(previousRange);
       
		newRange.lesserValue = slider.getModel().getSortedThumbs().get(slider.getModel().getThumbCount()-1);
		System.out.println("EQ color = " + newRange.lesserValue);
       newRange.equalValue = Color.white;
       newRange.greaterValue = previousRange.greaterValue;
		mapping.addPoint(maxValue, newRange);
		
		slider.repaint();
		repaint();
		
	}

	@Override
	protected void deleteButtonActionPerformed(ActionEvent evt) {
		final int selectedIndex = slider.getSelectedIndex();
		System.out.println("========== Selected = " + selectedIndex);

		if ((0 <= selectedIndex) && (slider.getModel().getThumbCount() > 1)) {
			slider.getModel().removeThumb(selectedIndex);
			mapping.removePoint(selectedIndex);
		}
		repaint();
	}

	private void initButtons() {
		addButton.addActionListener(
	            new AddPointListener(mapping, Color.white));
	}

	/**
	 * DOCUMENT ME!
	 */
	public void setSlider() {
		Dimension dim = new Dimension(600, 100);
		setPreferredSize(dim);
		setSize(dim);
		setMinimumSize(new Dimension(300, 80));
		slider.updateUI();

		slider.setComponentPopupMenu(menu);

		slider.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent arg0) {
			}

			public void mouseMoved(MouseEvent arg0) {
				// slider.setToolTipText((((VizMapperTrackRenderer) slider
				// .getTrackRenderer())).getToolTipForCurrentLocation(arg0
				// .getX(), arg0.getY()));
			}
		});

		slider.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
				} else {
					final JComponent selectedThumb = slider.getSelectedThumb();

					if (selectedThumb != null) {
						final Point location = selectedThumb.getLocation();
						double diff = Math.abs(location.getX() - e.getX());

						if ((diff < 8) && (e.getClickCount() == 2)) {
							final Color newColor = JColorChooser.showDialog(
									slider, "Choose new color...", Color.white);

							if (newColor != null) {
								slider.getModel().getThumbAt(
										slider.getSelectedIndex()).setObject(
										newColor);

								final ContinuousMapping cMapping = mapping;
								int selected = getSelectedPoint(slider
										.getSelectedIndex());
								cMapping.getPoint(selected).getRange().equalValue = newColor;

								final BoundaryRangeValues brv = new BoundaryRangeValues(
										cMapping.getPoint(selected).getRange().lesserValue,
										newColor, cMapping.getPoint(selected)
												.getRange().greaterValue);

								cMapping.getPoint(selected).setRange(brv);

								for (Object obj : ((ContinuousMapping) Cytoscape
										.getVisualMappingManager()
										.getVisualStyle()
										.getNodeAppearanceCalculator()
										.getCalculator(type).getMapping(0))
										.getAllPoints())
									System.out
											.println(((ContinuousMappingPoint) obj)
													.getRange().equalValue);

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

									cMapping.fireStateChanged();

									Cytoscape.getVisualMappingManager()
											.getNetworkView().redrawGraph(
													false, true);
									slider.repaint();
								}
							}
						}
					}
				}
			}

			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});

		double actualRange = Math.abs(minValue - maxValue);

		if (allPoints != null) {
			for (ContinuousMappingPoint point : allPoints) {
				BoundaryRangeValues bound = point.getRange();
				System.out.println("--------------- GOT point:  "
						+ point.getValue());

				slider
						.getModel()
						.addThumb(
								((Double) ((point.getValue() - minValue) / actualRange))
										.floatValue() * 100,
								(Color) bound.equalValue);
			}

			below = (Color) allPoints.get(0).getRange().lesserValue;
			above = (Color) allPoints.get(allPoints.size() - 1).getRange().greaterValue;
		}

		TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

		System.out.println("--------- VS = "
				+ Cytoscape.getVisualMappingManager().getVisualStyle()
						.getNodeAppearanceCalculator().getCalculator(
								VisualPropertyType.NODE_SHAPE) + " ----");

		CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(minValue,
				maxValue, (Color)below, (Color)above);
		//updateBelowAndAbove();
		
		slider.setThumbRenderer(thumbRend);
		slider.setTrackRenderer(gRend);
		slider.addMouseListener(new ThumbMouseListener());
	}
	
	private void updateBelowAndAbove() {
		ContinuousMappingPoint point;
		
		for(int i=0; i<mapping.getPointCount(); i++) {
			point = mapping.getPoint(i);
			point.getRange().greaterValue = above;
			point.getRange().lesserValue = below;
		}
	}
}
