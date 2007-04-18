package cytoscape.visual.ui;

import cytoscape.Cytoscape;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.multislider.Thumb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class C2CMappingEditor extends MappingEditorPanel2 {
    /**
     * Creates a new C2CMappingEditor object.
     *
     * @param type DOCUMENT ME!
     */
    public C2CMappingEditor(VisualPropertyType type) {
        super(type);
        setSlider();

        // TODO Auto-generated constructor stub
    }

    @Override
    protected void addButtonActionPerformed(ActionEvent evt) {
        slider.getModel()
              .addThumb(0.5f, 0f);
        slider.repaint();
    }

    @Override
    protected void deleteButtonActionPerformed(ActionEvent evt) {
        final int selectedIndex = slider.getSelectedIndex();
        System.out.println("========== Selected = " + selectedIndex);

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

        double actualRange;

        for (ContinuousMappingPoint point : allPoints) {
            //slider.getModel().addThumb(point.getValue(), arg1)
        }

        slider.getModel()
              .addThumb(10.0f, 10f);
        slider.getModel()
              .addThumb(40.0f, 30f);
        slider.getModel()
              .addThumb(80.0f, 100f);
        slider.getModel()
              .addThumb(30.0f, 50f);

        TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

        System.out.println("--------- VS = " +
            Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_SHAPE) +
            " ----");

        ContinuousTrackRenderer cRend = new ContinuousTrackRenderer(minValue,
                maxValue);

        slider.setThumbRenderer(thumbRend);
        slider.setTrackRenderer(cRend);
        slider.addMouseListener(new ThumbMouseListener());

        rotaryEncoder.updateUI();
        rotaryEncoder.getModel()
                     .addThumb(10f, 10f);
        rotaryEncoder.setThumbRenderer(
            new TriangleThumbRenderer(rotaryEncoder));
        rotaryEncoder.setTrackRenderer(new RotaryEncoder());
    }
}
