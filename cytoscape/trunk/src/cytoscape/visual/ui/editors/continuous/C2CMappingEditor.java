package cytoscape.visual.ui.editors.continuous;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;


/**
 * Continuous-Continuous mapping editor.
 *
 * @version 0.7
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * 
  */
public class C2CMappingEditor extends ContinuousMappingEditorPanel {
    /**
     * Creates a new C2CMappingEditor object.
     *
     * @param type DOCUMENT ME!
     */
    public C2CMappingEditor(VisualPropertyType type) {
        super(type);
        abovePanel.setVisible(false);
        belowPanel.setVisible(false);
        pack();
        setSlider();
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public static Object showDialog(final int width, final int height,
        final String title, VisualPropertyType type) {
        editor = new C2CMappingEditor(type);
        editor.setSize(new Dimension(width, height));
        editor.setTitle(title);
        editor.setAlwaysOnTop(true);
        editor.setLocationRelativeTo(Cytoscape.getDesktop());
        editor.setVisible(true);
        
        return editor;
    }
    
    /**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static ImageIcon getIcon(final int iconWidth, final int iconHeight,
            VisualPropertyType type) {
		editor = new C2CMappingEditor(type);
		ContinuousTrackRenderer rend = (ContinuousTrackRenderer)editor.slider.getTrackRenderer();
		rend.getRendererComponent(editor.slider);
		return rend.getTrackGraphicIcon(iconWidth, iconHeight);
	}

	public static ImageIcon getLegend(final int width, final int height, final VisualPropertyType type) {
		editor = new C2CMappingEditor(type);
		final ContinuousTrackRenderer rend = (ContinuousTrackRenderer)editor.slider.getTrackRenderer();
		rend.getRendererComponent(editor.slider);
		return rend.getLegend(width, height);
	}
	
    @Override
    protected void addButtonActionPerformed(ActionEvent evt) {
        BoundaryRangeValues newRange;

        if (mapping.getPointCount() == 0) {
            slider.getModel()
                  .addThumb(50f, 5f);

            newRange = new BoundaryRangeValues(below, 5f, above);
            mapping.addPoint(maxValue / 2, newRange);
            Cytoscape.getVisualMappingManager()
                     .getNetworkView()
                     .redrawGraph(false, true);

            slider.repaint();
            repaint();

            return;
        }

        // Add a new white thumb in the min.
        slider.getModel()
              .addThumb(100f, 5f);

        // Update continuous mapping
        final Double newVal = maxValue;

        // Pick Up first point.
        final ContinuousMappingPoint previousPoint = mapping.getPoint(mapping.getPointCount() -
                1);

        final BoundaryRangeValues previousRange = previousPoint.getRange();
        newRange = new BoundaryRangeValues(previousRange);

        newRange.lesserValue = slider.getModel()
                                     .getSortedThumbs()
                                     .get(slider.getModel().getThumbCount() -
                1);
        System.out.println("EQ color = " + newRange.lesserValue);
        newRange.equalValue = 5f;
        newRange.greaterValue = previousRange.greaterValue;
        mapping.addPoint(maxValue, newRange);

        updateMap();

        Cytoscape.getVisualMappingManager()
                 .getNetworkView()
                 .redrawGraph(false, true);

        slider.repaint();
        repaint();

    }

    @Override
    protected void deleteButtonActionPerformed(ActionEvent evt) {
        final int selectedIndex = slider.getSelectedIndex();

        if ((0 <= selectedIndex) && (slider.getModel()
                                               .getThumbCount() > 1)) {
            slider.getModel()
                  .removeThumb(selectedIndex);
            ((ContinuousTrackRenderer) slider.getTrackRenderer()).removePoint(selectedIndex);
        }
    }

    private void setSlider() {
        Dimension dim = new Dimension(600, 100);
        setPreferredSize(dim);
        setSize(dim);
        setMinimumSize(new Dimension(300, 80));
        slider.updateUI();

        slider.addMouseMotionListener(
            new MouseMotionListener() {
                public void mouseDragged(MouseEvent arg0) {
                }

                public void mouseMoved(MouseEvent arg0) {
                    //				slider.setToolTipText((((VizMapperTrackRenderer) slider
                    //						.getTrackRenderer())).getToolTipForCurrentLocation(arg0
                    //						.getX(), arg0.getY()));
                }
            });

        slider.addMouseListener(
            new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    //				Image icon = (Image) ((VizMapperTrackRenderer) slider
                    //						.getTrackRenderer()).getObjectInRange(e.getX(), e
                    //						.getY());
                    //				if (icon != null && e.getClickCount() == 2) {
                    //					JOptionPane.showMessageDialog(slider,
                    //							"Icon selection dialog will be displayed here.!",
                    //							"Select icon", JOptionPane.INFORMATION_MESSAGE);
                    //				}
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub
                }

                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub
                }

                public void mouseReleased(MouseEvent e) {
                }
            });

        double actualRange = Math.abs(minValue - maxValue);

        BoundaryRangeValues bound;
        Float fraction;

        List<Float> values = new ArrayList<Float>();

        for (ContinuousMappingPoint point : allPoints) {
            bound = point.getRange();

            fraction = ((Number) ((point.getValue() - minValue) / actualRange)).floatValue() * 100;
            slider.getModel()
                  .addThumb(
                fraction,
                ((Number) bound.equalValue).floatValue());
        }

        if (allPoints.size() != 0) {
            below = (Number) allPoints.get(0)
                                      .getRange().lesserValue;
            above = (Number) allPoints.get(allPoints.size() - 1)
                                      .getRange().greaterValue;
        } else {
            below = 5f;
            above = 10f;
        }

        /*
         * get min and max for the value object
         */
        TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

        ContinuousTrackRenderer cRend = new ContinuousTrackRenderer(type,
                minValue, maxValue, (Number) below, (Number) above);

        slider.setThumbRenderer(thumbRend);
        slider.setTrackRenderer(cRend);
        slider.addMouseListener(new ThumbMouseListener());
    }
}
