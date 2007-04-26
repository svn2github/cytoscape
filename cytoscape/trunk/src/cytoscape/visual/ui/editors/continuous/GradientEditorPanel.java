package cytoscape.visual.ui.editors.continuous;

import cytoscape.Cytoscape;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.AddPointListener;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/**
 * Gradient editor.
 *
 * @version 0.7
 * @since Cytoscpae 2.5
 * @author kono
 */
public class GradientEditorPanel extends ContinuousMappingEditorPanel
    implements PropertyChangeListener {
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

        belowPanel.addPropertyChangeListener(this);
        abovePanel.addPropertyChangeListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public static void showDialog(final int width, final int height,
        final String title, VisualPropertyType type) {
        editor = new GradientEditorPanel(type);
        editor.setSize(new Dimension(width, height));
        editor.setTitle(title);
        editor.setAlwaysOnTop(true);
        editor.setLocationRelativeTo(Cytoscape.getDesktop());
        editor.setVisible(true);
        editor.repaint();
    }

    @Override
    protected void addButtonActionPerformed(ActionEvent evt) {
        BoundaryRangeValues newRange;

        if (mapping.getPointCount() == 0) {
            slider.getModel()
                  .addThumb(50f, Color.white);

            newRange = new BoundaryRangeValues(below, Color.white, above);
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
              .addThumb(100f, Color.white);

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
        newRange.equalValue = Color.white;
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
        System.out.println("========== Selected = " + selectedIndex);

        if (0 <= selectedIndex) {
            slider.getModel()
                  .removeThumb(selectedIndex);
            mapping.removePoint(selectedIndex);
            updateMap();
            mapping.fireStateChanged();

            Cytoscape.getVisualMappingManager()
                     .getNetworkView()
                     .redrawGraph(false, true);
            repaint();
        }
    }

    private void initButtons() {
        addButton.addActionListener(new AddPointListener(mapping, Color.white));
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

        slider.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                    } else {
                        System.out.println(
                            "Thumb Click------------------------> " +
                            e.getSource());

                        final JComponent selectedThumb = slider.getSelectedThumb();

                        if (selectedThumb != null) {
                            final Point location = selectedThumb.getLocation();
                            double diff = Math.abs(location.getX() - e.getX());

                            if (e.getClickCount() == 2) {
                                final Color newColor = JColorChooser.showDialog(slider,
                                        "Choose new color...", Color.white);

                                if (newColor != null) {
                                    slider.getModel()
                                          .getThumbAt(
                                        slider.getSelectedIndex())
                                          .setObject(newColor);

                                    final ContinuousMapping cMapping = mapping;
                                    int selected = getSelectedPoint(
                                            slider.getSelectedIndex());

                                    cMapping.getPoint(selected)
                                            .getRange().equalValue = newColor;

                                    final BoundaryRangeValues brv = new BoundaryRangeValues(cMapping.getPoint(
                                                selected)
                                                                                                    .getRange().lesserValue,
                                            newColor,
                                            cMapping.getPoint(selected)
                                                    .getRange().greaterValue);

                                    cMapping.getPoint(selected)
                                            .setRange(brv);

                                    int numPoints = cMapping.getAllPoints()
                                                            .size();

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
                                                 .getNetworkView()
                                                 .redrawGraph(false, true);
                                        slider.repaint();
                                    }
                                }
                            }
                        }
                    }
                }
            });

        double actualRange = Math.abs(minValue - maxValue);

        if (allPoints != null) {
            for (ContinuousMappingPoint point : allPoints) {
                BoundaryRangeValues bound = point.getRange();
                System.out.println("--------------- GOT point:  " +
                    point.getValue());

                slider.getModel()
                      .addThumb(((Double) ((point.getValue() - minValue) / actualRange)).floatValue() * 100,
                    (Color) bound.equalValue);
            }

            if (allPoints.size() != 0) {
                below = (Color) allPoints.get(0)
                                         .getRange().lesserValue;
                above = (Color) allPoints.get(allPoints.size() - 1)
                                         .getRange().greaterValue;
            } else {
                below = Color.black;
                above = Color.white;
            }

            setSidePanelIconColor((Color) below, (Color) above);
        }

        TriangleThumbRenderer thumbRend = new TriangleThumbRenderer(slider);

        System.out.println("--------- VS = " +
            Cytoscape.getVisualMappingManager().getVisualStyle().getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_SHAPE) +
            " ----");

        CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(minValue,
                maxValue, (Color) below, (Color) above,
                mapping.getControllingAttributeName());
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
        // TODO Auto-generated method stub
        if (e.getPropertyName()
                 .equals(BelowAndAbovePanel.COLOR_CHANGED)) {
            String sourceName = ((BelowAndAbovePanel) e.getSource()).getName();

            if (sourceName.equals("abovePanel"))
                this.above = e.getNewValue();
            else
                this.below = e.getNewValue();

            final CyGradientTrackRenderer gRend = new CyGradientTrackRenderer(minValue,
                    maxValue,
                    (Color) below,
                    (Color) above,
                    mapping.getControllingAttributeName());
            slider.setTrackRenderer(gRend);

            repaint();
        }
    }
}
