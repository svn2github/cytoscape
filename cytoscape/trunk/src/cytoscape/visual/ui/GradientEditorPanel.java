package cytoscape.visual.ui;

import cytoscape.Cytoscape;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.multislider.Thumb;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GradientEditorPanel extends MappingEditorPanel2 {
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
     * @param type DOCUMENT ME!
     */
    public GradientEditorPanel(VisualPropertyType type) {
        super(type);
        iconPanel.setVisible(false);
        setSlider();
    }

    @Override
    protected void addButtonActionPerformed(ActionEvent evt) {
        slider.getModel()
              .addThumb(0.2f, Color.white);
        repaint();
    }

    @Override
    protected void deleteButtonActionPerformed(ActionEvent evt) {
        final int selectedIndex = slider.getSelectedIndex();
        System.out.println("========== Selected = " + selectedIndex);

        if ((0 <= selectedIndex) && (slider.getModel()
                                               .getThumbCount() > 1))
            slider.getModel()
                  .removeThumb(selectedIndex);

        repaint();
    }

    private void initSlider() {
    }

    private void setSlider() {
        Dimension dim = new Dimension(600, 100);
        setPreferredSize(dim);
        setSize(dim);
        setMinimumSize(new Dimension(300, 80));
        slider.updateUI();

        slider.setComponentPopupMenu(menu);

        slider.addMouseMotionListener(
            new MouseMotionListener() {
                public void mouseDragged(MouseEvent arg0) {
                }

                public void mouseMoved(MouseEvent arg0) {
                    // slider.setToolTipText((((VizMapperTrackRenderer) slider
                    // .getTrackRenderer())).getToolTipForCurrentLocation(arg0
                    // .getX(), arg0.getY()));
                }
            });

        slider.addMouseListener(
            new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                    } else {
                        final JComponent selectedThumb = slider.getSelectedThumb();

                        if (selectedThumb != null) {
                            final Point location = selectedThumb.getLocation();
                            double diff = Math.abs(location.getX() - e.getX());
                            System.out.print("Pos = " +
                                slider.getSelectedThumb().getLocation() +
                                ", pointer = " + e.getX());

                            if ((diff < 8) && (e.getClickCount() == 2)) {
                                final Color newColor = JColorChooser.showDialog(slider,
                                        "Choose nee color...", Color.white);
                                slider.getModel()
                                      .getThumbAt(slider.getSelectedIndex())
                                      .setObject(newColor);
                            }

                            System.out.print("Pos = " +
                                slider.getSelectedThumb().getLocation() +
                                ", pointer = " + e.getX());
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

        double actualRange;

        for (ContinuousMappingPoint point : allPoints) {
            // slider.getModel().addThumb(point.getValue(), arg1)
        }

        slider.getModel()
              .addThumb(10.0f, Color.red);
        slider.getModel()
              .addThumb(40.0f, Color.white);
        slider.getModel()
              .addThumb(80.0f, Color.green);
        slider.getModel()
              .addThumb(30.0f, Color.yellow);

        TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);
        thumbRend.addMouseListener(
            new MouseListener() {
                public void mouseClicked(MouseEvent arg0) {
                    System.out.println("--------------- GG ");
                }

                public void mouseEntered(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                public void mouseExited(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                public void mousePressed(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }

                public void mouseReleased(MouseEvent arg0) {
                    // TODO Auto-generated method stub
                }
            });
        System.out.println("--------- VS = " +
            Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_SHAPE) +
            " ----");

        CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(minValue,
                maxValue, Color.black, Color.blue);

        slider.setThumbRenderer(thumbRend);
        slider.setTrackRenderer(gRend);
        slider.addMouseListener(new ThumbMouseListener());
    }
}
