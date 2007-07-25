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
import infovis.visualization.color.EqualizedOrderedColor;
import infovis.visualization.color.OrderedColor;
import infovis.visualization.render.VisualColor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Color Control Panel for Ordered Colors.
 * 
 * <p>
 * See <a href="http://colorbrewer.org">http://colorbrewer.org </a>
 * </p>
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.20 $
 */
public class OrderedColorControlPanel extends ColorVisualizationControlPanel {
    protected JButton              startButton;
    protected JButton              endButton;
    protected DefaultComboBoxModel colorSchemesModel;
    protected JComboBox            colorSchemes;
    protected JCheckBox            equalized;
    protected JCheckBox            setDefaultButton;
    protected JCheckBox            equalizeButton;
    private static ColorScheme[]   pschemes;

    protected OrderedColorControlPanel() {
    }

    /**
     * Creates a new OrderedColorControlPanel object.
     * 
     * @param visualization
     *            the Visualization
     */
    public OrderedColorControlPanel(Visualization visualization) {
        super(visualization);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Box b1 = Box.createHorizontalBox();
        startButton = new JButton("Minimum");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = chooseColor(startButton.getBackground());
                if (!c.equals(startButton.getBackground())) {
                    OrderedColor oc = getOrderedColor();
                    oc.setStart(c);
//                    if (setDefaultButton.isSelected()) {
                        // OrderedColor.setDefaultStart(c);
                        // TODO fix!
//                    }
                    update();
                    getVisualization().repaint();
                }
            }
        });
        b1.add(startButton);

        endButton = new JButton("Maximum");
        endButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = chooseColor(startButton.getBackground());
                if (!c.equals(endButton.getBackground())) {
                    OrderedColor oc = getOrderedColor();
                    oc.setEnd(c);
//                    if (setDefaultButton.isSelected()) {
                        // OrderedColor.setDefaultEnd(c);
                        // TODO fix/
//                    }
                    update();
                    getVisualization().repaint();
                }
            }
        });
        b1.add(endButton);

        Box b2 = Box.createHorizontalBox();

        colorSchemesModel = new DefaultComboBoxModel(getSchemes());
        colorSchemes = new JComboBox(colorSchemesModel);
        colorSchemes.setRenderer(new ColorSchemeListCellRenderer());
        colorSchemes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ColorScheme scheme = (ColorScheme) colorSchemes
                        .getSelectedItem();
                OrderedColor oc = getOrderedColor();
                // oc.setStart(scheme.getStart());
                // oc.setEnd(scheme.getEnd());
                oc.setRamp(scheme.getRamp());
                if (setDefaultButton.isSelected()) {
                    OrderedColor.setDefaultRamp(scheme.getRamp());
                }
                update();
                getVisualization().repaint();
            }
        });
        b2.add(colorSchemes);

        setDefaultButton = new JCheckBox("Set as Default");
        setDefaultButton.setSelected(true);
        setDefaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (setDefaultButton.isSelected()) {
                    ColorScheme scheme = 
                        (ColorScheme) colorSchemes.getSelectedItem();
                    // OrderedColor.setDefaults(startButton.getBackground(),
                    // endButton.getBackground());
                    OrderedColor.setDefaultRamp(scheme.getRamp());
                }
            }
        });
        b2.add(setDefaultButton);
        
        equalizeButton = new JCheckBox("Equalize");
        equalizeButton.setSelected(true);
        equalizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EqualizedOrderedColor ec = 
                    (EqualizedOrderedColor)getOrderedColor();
                ec.setUsingQuantiles(equalizeButton.isSelected());
                getVisualization().repaint();
            }
        });
        b2.add(equalizeButton);
        add(b1);
        add(b2);

        update();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public OrderedColor getOrderedColor() {
        return (OrderedColor) getColorVisualization();
    }

    public static ColorScheme[] getSchemes() {
        if (pschemes == null) {
            pschemes = ColorScheme.loadColorSchemes("colorschemes");
        }
        return pschemes;
    }

    public static int getColorSchemeOffset(String name) {
        getSchemes();
        for (int i = 0; i < pschemes.length; i++) {
            ColorScheme cs = pschemes[i];
            if (cs.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean setColorScheme(int index, Visualization vis) {
        if (index < 0 || index >= getSchemes().length)
            return false;
        VisualColor vc = VisualColor.get(vis);
        if (vc == null)
            return false;
        ColorVisualization cv = vc.getColorVisualization();
        if (!(cv instanceof OrderedColor)) {
            return false;
        }
        OrderedColor oc = (OrderedColor) cv;
        ColorScheme cs = getSchemes()[index];
        oc.setRamp(cs.getRamp());
        return true;
    }

    public static boolean setColorScheme(String name, Visualization vis) {
        return setColorScheme(getColorSchemeOffset(name), vis);
    }

    public ColorScheme findScheme(Color start, Color end) {
        for (int i = 0; i < colorSchemesModel.getSize(); i++) {
            ColorScheme s = (ColorScheme) colorSchemesModel.getElementAt(i);
            if (s.getStart().equals(start) && s.getEnd().equals(end)) {
                return s;
            }
        }
        ColorScheme def = new ColorScheme("Scheme#"
                + colorSchemesModel.getSize(), start, end);
        colorSchemesModel.addElement(def);
        return def;
    }

    /**
     * DOCUMENT ME!
     */
    public void update() {
        OrderedColor orderedColor = getOrderedColor();
        Color start = orderedColor.getStart();
        Color end = orderedColor.getEnd();
        startButton.setBackground(start);
        startButton.setForeground(ColorVisualization.colorComplement(start));
        endButton.setBackground(end);
        endButton.setForeground(ColorVisualization.colorComplement(end));
        colorSchemes.setSelectedItem(findScheme(start, end));
        equalizeButton.setEnabled(orderedColor instanceof EqualizedOrderedColor);
    }
}
