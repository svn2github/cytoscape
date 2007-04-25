package cytoscape.visual.ui.editors.continuous;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.imageio.ImageIO;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class C2DMappingEditor extends ContinuousMappingEditorPanel {
    /**
     * Creates a new C2DMappingEditor object.
     *
     * @param type DOCUMENT ME!
     */
    public C2DMappingEditor(VisualPropertyType type) {
        super(type);
        this.iconPanel.setVisible(false);
        setSlider();
    }
    
    public static void showDialog(final int width, final int height, final String title, VisualPropertyType type) {
		editor = new C2DMappingEditor(type);
		editor.setSize(new Dimension(width, height));
		editor.setTitle(title);
		editor.setAlwaysOnTop(true);
		editor.setLocationRelativeTo(Cytoscape.getDesktop());
		editor.setVisible(true);
	}

    @Override
    protected void addButtonActionPerformed(ActionEvent evt) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void deleteButtonActionPerformed(ActionEvent evt) {
        // TODO Auto-generated method stub
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
                    // slider.setToolTipText((((VizMapperTrackRenderer) slider
                    // .getTrackRenderer())).getToolTipForCurrentLocation(arg0
                    // .getX(), arg0.getY()));
                }
            });

        slider.addMouseListener(
            new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                    // Image icon = (Image) ((VizMapperTrackRenderer) slider
                    // .getTrackRenderer()).getObjectInRange(e.getX(), e
                    // .getY());
                    // if (icon != null && e.getClickCount() == 2) {
                    // JOptionPane.showMessageDialog(slider,
                    // "Icon selection dialog will be displayed here.!",
                    // "Select icon", JOptionPane.INFORMATION_MESSAGE);
                    // }
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

        //		double actualRange;
        //		for (ContinuousMappingPoint point : allPoints) {
        //			// slider.getModel().addThumb(point.getValue(), arg1)
        //		}
        Image icon1;
        Image icon2;
        Image icon3;
        Image icon4;
        Image icon5;

        try {
            icon1 = ImageIO.read(
                    Cytoscape.class.getResource(
                        "visual/ui/images/round_rect.jpg"));
            icon2 = ImageIO.read(
                    Cytoscape.class.getResource("visual/ui/images/triangle.jpg"));
            icon3 = ImageIO.read(
                    Cytoscape.class.getResource("visual/ui/images/ellipse.jpg"));
            icon4 = ImageIO.read(
                    Cytoscape.class.getResource("visual/ui/images/octagon.jpg"));
            icon5 = ImageIO.read(
                    Cytoscape.class.getResource("visual/ui/images/diamond.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            icon1 = null;
            icon2 = null;
            icon3 = null;
            icon4 = null;
            icon5 = null;
        }

        slider.getModel()
              .addThumb(10.0f, icon1);
        slider.getModel()
              .addThumb(20.0f, icon2);
        slider.getModel()
              .addThumb(40.0f, icon3);
        slider.getModel()
              .addThumb(80.0f, icon4);

        TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

        System.out.println("--------- VS = " +
            Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_SHAPE) +
            " ----");

        DiscreteTrackRenderer dRend = new DiscreteTrackRenderer(minValue,
                maxValue, icon5, null);

        slider.setThumbRenderer(thumbRend);
        slider.setTrackRenderer(dRend);
        slider.addMouseListener(new ThumbMouseListener());
    }
}
