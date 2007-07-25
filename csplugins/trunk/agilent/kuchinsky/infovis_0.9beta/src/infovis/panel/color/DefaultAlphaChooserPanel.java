/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.Icon;
import javax.swing.JSlider;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class DefaultAlphaChooserPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DefaultAlphaChooserPanel
    extends AbstractColorChooserPanel
    implements ChangeListener {
    protected JSlider alphaSlider;
    protected JSpinner alphaField;
    protected boolean isAdjusting = false;

    public DefaultAlphaChooserPanel() {
    }
    
    private void setColor(Color newColor) {
        int alpha = newColor.getAlpha();
        
        if (alphaSlider.getValue() != alpha) {
            alphaSlider.setValue(alpha);
        }
        if (((Integer)alphaField.getValue()).intValue() != alpha) {
            alphaField.setValue(new Integer(alpha));
        }
    }

    public String getDisplayName() {
        String name = UIManager.getString("ColorChooser.alphaNameText");
        if (name == null)
            name = "Alpha";
        return name;
    }
    
    public int getMnemonic() {
        Object value = UIManager.get("ColorChooser.alphaMnemonic");
        if (value instanceof Integer) {
            return ((Integer)value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            } catch(NumberFormatException nfe) {
                ; // ignore
            }
        }
        return 'a';
    }

    public void updateChooser() {
        if (! isAdjusting) {
            isAdjusting = true;
            setColor(getColorFromModel());
            isAdjusting = false;
        }
    }

    protected void buildChooser() {
        setLayout(new BorderLayout());
        Color color = getColorFromModel();
        
        Box box = new Box(BoxLayout.X_AXIS);
        add(box, BorderLayout.CENTER);
        
        JLabel l = new JLabel("Alpha");
        box.add(l);
        alphaSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, color.getAlpha());
        alphaSlider.setMajorTickSpacing(85);
        alphaSlider.setMinorTickSpacing(17);
        alphaSlider.setPaintTicks(true);
        alphaSlider.setPaintLabels(true);
        box.add(alphaSlider);
        
        alphaField = new JSpinner(
            new SpinnerNumberModel(color.getAlpha(), 0, 255, 1));
        l.setLabelFor(alphaSlider);
        box.add(alphaField);
        alphaSlider.addChangeListener(this);
        alphaSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSlider && ! isAdjusting) {
            int alpha = alphaSlider.getValue();
            Color orig = getColorFromModel();
            Color color = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(), alpha);
            getColorSelectionModel().setSelectedColor(color);
        }
        else if (e.getSource() instanceof JSpinner && ! isAdjusting) {
            int alpha = ((Integer)alphaField.getValue()).intValue();
            Color orig = getColorFromModel();
            Color color = new Color(orig.getRed(), orig.getGreen(), orig.getBlue(), alpha);
            getColorSelectionModel().setSelectedColor(color);
        }
    }

}
